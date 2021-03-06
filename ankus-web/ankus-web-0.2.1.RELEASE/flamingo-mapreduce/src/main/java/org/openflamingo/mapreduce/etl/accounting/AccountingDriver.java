package org.openflamingo.mapreduce.etl.accounting;

import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;
import org.apache.hadoop.util.ToolRunner;
import org.openflamingo.mapreduce.core.AbstractJob;
import org.openflamingo.mapreduce.core.Delimiter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

import static org.openflamingo.mapreduce.core.Constants.JOB_FAIL;
import static org.openflamingo.mapreduce.core.Constants.JOB_SUCCESS;

/**
 * 하나 이상의 입력 파일을 받아서 합치는 Aggregation ETL Driver.
 * 이 MapReduce ETL은 다음의 파라미터를 가진다.
 * <ul>
 * <li><tt>lineCountPerFile (c)</tt> - 파일당 라인수 측정 여부 (선택) (기본값 false)</li>
 * </ul>
 *
 * @author Edward KIM
 * @author Seo Ji Hye
 * @since 0.1
 */
public class AccountingDriver extends AbstractJob {

	/**
	 * SLF4J API
	 */
	private static final Logger logger = LoggerFactory.getLogger(AccountingDriver.class);

	public static void main(String[] args) throws Exception {
		int res = ToolRunner.run(new AccountingDriver(), args);
		System.exit(res);
	}

	@Override
	public int run(String[] args) throws Exception {
		addInputOption();
		addOutputOption();

		addOption("inputDelimiter", "id", "입력 컬럼 구분자", Delimiter.COMMA.getDelimiter());
		addOption("outputDelimiter", "od", "출력 컬럼 구분자", Delimiter.COMMA.getDelimiter());
		addOption("columnSize", "cs", "컬럼의 개수", true);
		addOption("expression", "ex", "실행할 수식 파일의 경로 입력", true);

		Map<String, String> parsedArgs = parseArguments(args);
		if (parsedArgs == null) {
			return JOB_FAIL;
		}

		Job job = prepareJob(getInputPath(), getOutputPath(),
			TextInputFormat.class,
			AccountingMapper.class,
			NullWritable.class,
			Text.class,
			TextOutputFormat.class);

		job.getConfiguration().set("inputDelimiter", parsedArgs.get("--inputDelimiter"));
		job.getConfiguration().set("outputDelimiter", parsedArgs.get("--outputDelimiter"));
		job.getConfiguration().set("columnSize", parsedArgs.get("--columnSize"));
		job.getConfiguration().set("expression", parsedArgs.get("--expression"));

		return job.waitForCompletion(true) ? JOB_SUCCESS : JOB_FAIL;
	}
}