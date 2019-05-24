import com.ts.invoice.logic.Converter;
import com.ts.invoice.logic.Parser;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;


import static com.ts.invoice.utils.Const.INVALID_STR;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;

@RunWith(VertxUnitRunner.class)
public class ParserUnitTest {

	static Converter converter ;
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		Parser p = new Parser();
		converter = new Converter(p);
	}
	@Test
	public void testSuccessParsing() {

		final String line1 = " _  _  _        _     _  _ ";
		final String line2 = "|_ | || |  ||_| _|  ||_ |_ ";
		final String line3 = "|_||_||_|  |  | _|  | _| _|";
		String[] data = new String[] {line1,line2,line3,""};

		assertThat(converter.Convert(data), equalTo("600143155"));

	}

	@Test
	public void testIllegalParsing() {

		final String line1 = " _  _  _        _     _  _ ";
		final String line2 = "|_ | || |  ||_| _|  ||  |_ ";
		final String line3 = "|_||_||_|  |  | _|  | _| _|";
		String[] data = new String[] {line1,line2,line3,""};

		assertThat(converter.Convert(data), equalTo("6001431?5" + INVALID_STR));

	}

	@Test
	public void testIllegalParsingBlankLineMissing() {

		final String line1 = " _  _  _        _     _  _ ";
		final String line2 = "|_ | || |  ||_| _|  ||  |_ ";
		final String line3 = "|_||_||_|  |  | _|  | _| _|";
		String[] data = new String[] {line1,line2,line3};

		assertThat(converter.Convert(data), equalTo(INVALID_STR));

	}

	//TODO : Write more tests...........


}
