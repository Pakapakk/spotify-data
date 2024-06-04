import java.io.IOException;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

public class SpotifyWordCount {

    // Mapper Class
    public static class SpotifyMapper extends Mapper<LongWritable, Text, Text, IntWritable> {
        private final static IntWritable one = new IntWritable(1);
        private Text word = new Text();

        @Override
        protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
            // Skip the header row
            if (key.get() == 0 && value.toString().contains("id")) {
                return;
            }

            // Split the line into columns
            String[] fields = value.toString().split(",");
            if (fields.length > 1) {
                // For example, we can use 'artists' as the key
                String artist = fields[2].replaceAll("\\[|\\]|'", "").trim(); // [2] -> artists
                word.set(artist);
                context.write(word, one);
            }
        }
    }

    // Reducer Class
    public static class SpotifyReducer extends Reducer<Text, IntWritable, Text, IntWritable> {
        @Override
        protected void reduce(Text key, Iterable<IntWritable> values, Context context) throws IOException, InterruptedException {
            int sum = 0;
            for (IntWritable val : values) {
                sum += val.get();
            }
            context.write(key, new IntWritable(sum));
        }
    }

    // Driver Class
    public static void main(String[] args) throws Exception {
        if (args.length != 2) {
            System.err.println("Usage: SpotifyWordCount <input path> <output path>");
            System.exit(-1);
        }

        Configuration conf = new Configuration();
        Job job = Job.getInstance(conf, "spotify artist count");
        job.setJarByClass(SpotifyWordCount.class);
        job.setMapperClass(SpotifyMapper.class);
        job.setCombinerClass(SpotifyReducer.class); // Optional combiner
        job.setReducerClass(SpotifyReducer.class);
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(IntWritable.class);

        FileInputFormat.addInputPath(job, new Path(args[0]));
        FileOutputFormat.setOutputPath(job, new Path(args[1]));

        System.exit(job.waitForCompletion(true) ? 0 : 1);
    }
}