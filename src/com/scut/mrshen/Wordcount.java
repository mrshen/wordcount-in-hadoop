package com.scut.mrshen;

import java.io.IOException;
import java.util.StringTokenizer;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

public class Wordcount {

	public static class wcMapper extends Mapper<Object, Text, Text, IntWritable> {
		private static final IntWritable one = new IntWritable(1);
		@Override
		protected void map(Object key, Text value,
				Mapper<Object, Text, Text, IntWritable>.Context context)
				throws IOException, InterruptedException {
			// TODO Auto-generated method stub
//			super.map(key, value, context);
			StringTokenizer tokens = new StringTokenizer(value.toString().trim());
			while(tokens.hasMoreTokens()) {
				context.write(new Text(tokens.nextToken()), one);
			}
		}
		
	}
	
	public static class wcReduce extends Reducer<Text, IntWritable, Text, IntWritable> {
		private IntWritable value = new IntWritable();
		@Override
		protected void reduce(Text arg0, Iterable<IntWritable> arg1,
				Reducer<Text, IntWritable, Text, IntWritable>.Context arg2)
				throws IOException, InterruptedException {
			// TODO Auto-generated method stub
//			super.reduce(arg0, arg1, arg2);
			int sum = 0;
			for(IntWritable val : arg1) {
				sum += val.get();
			}
			value.set(sum);
			arg2.write(arg0, value);
		}
		
	}
	
	public static void main(String[] args) throws IOException, ClassNotFoundException, InterruptedException {
		// TODO Auto-generated method stub
		Configuration conf = new Configuration();
		Job job = Job.getInstance(conf, "word count");
		
		job.setJarByClass(Wordcount.class);
		
		job.setMapperClass(wcMapper.class);
		job.setMapOutputKeyClass(Text.class);
		job.setMapOutputValueClass(IntWritable.class);
		
		job.setCombinerClass(wcReduce.class);
		job.setReducerClass(wcReduce.class);
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(IntWritable.class);
		
		FileInputFormat.addInputPath(job, new Path(args[0]));
		FileOutputFormat.setOutputPath(job, new Path(args[1]));
		
		System.exit(job.waitForCompletion(true)?0:1);
	}

}
