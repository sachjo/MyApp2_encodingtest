package sachjo.myapp2encodingtest;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.CoderResult;
import java.nio.charset.spi.CharsetProvider;
import java.util.Collections;
import java.util.Iterator;

public class EUC_JP_OpenExtProvider extends CharsetProvider {

	public static final String CHARSET_NAME = "EUC-JP-OpenExt";
	public static final String CHARSET_ALIAS = "EUC_JP_OpenExt";


	private final Charset charset;

	public EUC_JP_OpenExtProvider() {
		charset = new EUC_JP_OpenExt();
	}

	public Iterator<Charset> charsets() {
		return Collections.singleton(charset).iterator();
	}

	public Charset charsetForName(String charsetName) {
		if (charsetName.equalsIgnoreCase(charset.name())) return charset;
		for (String alias: charset.aliases()) {
			if (charsetName.equalsIgnoreCase(alias)) return charset;
		}
		return null;
	}

}