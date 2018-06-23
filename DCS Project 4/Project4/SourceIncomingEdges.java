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

public class SourceIncomingEdges {

	public static class TokenizerMapper extends Mapper<Object, Text, IntWritable, Text> {

		private IntWritable documentId = new IntWritable();

		public void map(Object key, Text value, Context context) throws IOException, InterruptedException {
			StringTokenizer itr = new StringTokenizer(value.toString());
			while (itr.hasMoreTokens()) {
				String incomingDocumentID = itr.nextToken().toString();
				documentId.set(Integer.parseInt(itr.nextToken()));
				context.write(documentId, new Text(incomingDocumentID));
			}
		}
	}

	public static class InOutDegreeReducer extends Reducer<IntWritable, Text, IntWritable, Text> {
		private Text result = new Text();

		public void reduce(IntWritable key, Iterable<Text> values, Context context)
				throws IOException, InterruptedException {
			HashSet<String > st = new HashSet<String>();
			for (Text val : values) {
				st.add(val.toString());
			}
			String a = st.toString();
			result.set(a.substring(1, a.length()-1));
			context.write(key, result);
		}
	}

	public static void main(String[] args) throws Exception {
		Configuration conf = new Configuration();
		Job job = Job.getInstance(conf, "Source Incoming Edges");
		job.setJarByClass(SourceIncomingEdges.class);
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