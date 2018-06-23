package code;

import java.io.IOException;
import java.util.HashSet;
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

public class InOutDegree {

	public static class TokenizerMapper extends Mapper<Object, Text, IntWritable, Text> {

		private final static IntWritable one = new IntWritable(1);
		private final static Text IN = new Text("IN");
		private final static Text OUT = new Text("OUT");

		private IntWritable documentId = new IntWritable();

		HashSet<String> set = new HashSet<String>();

		public void map(Object key, Text value, Context context) throws IOException, InterruptedException {
			StringTokenizer itr = new StringTokenizer(value.toString());
			while (itr.hasMoreTokens()) {

				int sint = Integer.parseInt(itr.nextToken());
				int dint = Integer.parseInt(itr.nextToken());

				String s = Integer.toString(sint) + "-" + Integer.toString(dint);
				if (set.contains(s)) {
					;
				} else {
					set.add(s);
					documentId.set(sint);
					context.write(documentId, OUT);
					documentId.set(dint);
					context.write(documentId, IN);
				}

			}
		}
	}

	public static class InOutDegreeReducer extends Reducer<IntWritable, Text, IntWritable, Text> {
		private Text result = new Text();

		public void reduce(IntWritable key, Iterable<Text> values, Context context)
				throws IOException, InterruptedException {
			int outDegree = 0;
			int inDegree = 0;
			boolean changed = false;
			for (Text val : values) {
				changed = true;
				if (val.toString().equals("IN")) {
					inDegree++;
				} else if (val.toString().equals("OUT")) {
					outDegree++;
				} else {
					result.set(val);
					changed = false;

				}
			}
			if (changed)
				result.set(outDegree + " " + inDegree);
			context.write(key, result);
		}
	}

	public static void main(String[] args) throws Exception {
		Configuration conf = new Configuration();
		Job job = Job.getInstance(conf, "word count");

		job.setJarByClass(InOutDegree.class);
		job.setMapperClass(TokenizerMapper.class);
		job.setCombinerClass(InOutDegreeReducer.class);
		job.setReducerClass(InOutDegreeReducer.class);
		job.setOutputKeyClass(IntWritable.class);
		job.setOutputValueClass(Text.class);
		FileInputFormat.addInputPath(job, new Path(args[0]));
		FileOutputFormat.setOutputPath(job, new Path(args[1]));
		System.exit(job.waitForCompletion(true) ? 0 : 1);
	}
}