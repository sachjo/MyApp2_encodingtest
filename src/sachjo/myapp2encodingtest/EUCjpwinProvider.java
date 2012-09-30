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

public class EUCjpwinProvider extends CharsetProvider {

	public static final String CHARSET_NAME = "EUC-JPwin";
	public static final String CHARSET_ALIAS = "EUC_JPwin";

	private static final int MS932 = 0;
	private static final int EUCJP = 1;
	private static final char[][] chartable = {
		{ (char)0xFF5E, (char)0x301C }, //�` WAVE_DASH
		{ (char)0x2015, (char)0x2014 }, //�\ EM_DASH
		{ (char)0x2225, (char)0x2016 }, //�a DOUBLE_VERTICAL_LINE
		{ (char)0xFF0D, (char)0x2212 }, //�| MINUS_SIGN
		{ (char)0xFFE0, (char)0x00A2 }, //�� CENT_SIGN
		{ (char)0xFFE1, (char)0x00A3 }, //�� POUND_SIGN
		{ (char)0xFFE2, (char)0x00AC }, //�� NOT_SIGN
		{ (char)0xFFE3, (char)0x203E }, //�P OVER_LINE
		{ (char)0xFFE4, (char)0x00A6 }, //�U BROKEN_BAR
		{ (char)0xFFE5, (char)0x00A5 }  //�� YEN_SIGN
	};

	private static char convertedChar(char source, int from, int to) {
		for (int i = 0; i < chartable.length; i++) {
			if (source == chartable[i][from]) {
				return chartable[i][to];
			}
		}
		return source;
	}

	private final Charset charset;

	public EUCjpwinProvider() {
		charset = new EUCjpwin(CHARSET_NAME, new String[] {CHARSET_ALIAS});
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

	/**
	 * 
	 */
	public static class EUCjpwin extends Charset {

		private final Charset base = Charset.forName("EUC-JP");

		public EUCjpwin(String canonicalName, String[] aliases) {
			super(canonicalName, aliases);
		}

		public boolean contains(Charset cs) {
			return cs.equals(this);
		}

		public CharsetDecoder newDecoder() {
			return new EUCjpwinDecoder(this, base.newDecoder());
		}

		public CharsetEncoder newEncoder() {
			return new EUCjpwinEncoder(this, base.newEncoder());
		}
	}

	/**
	 * 
	 */
	public static class EUCjpwinEncoder extends CharsetEncoder {

		private final CharsetEncoder base;

		public EUCjpwinEncoder(Charset cs, CharsetEncoder base) {
			super(cs, base.averageBytesPerChar(), base.maxBytesPerChar());
			this.base = base;
		}

		protected CoderResult encodeLoop(CharBuffer in, ByteBuffer out) {
			if (in.hasRemaining()) {
				int length = in.length();
				CharBuffer converted = CharBuffer.allocate(length);
				for (int i = 0; i < length; i++) {
					converted.put(i, convertedChar(in.charAt(i), MS932, EUCJP));
				}
				CoderResult result = base.encode(converted, out, false);
				in.position(in.position() + converted.position());
				return result;
			}
			return CoderResult.UNDERFLOW;
		}
	}

	/**
	 * 
	 */
	public static class EUCjpwinDecoder extends CharsetDecoder {

		private final CharsetDecoder base;

		public EUCjpwinDecoder(Charset cs, CharsetDecoder base) {
			super(cs, base.averageCharsPerByte(), base.maxCharsPerByte());
			this.base = base;
		}

		protected CoderResult decodeLoop(ByteBuffer in, CharBuffer out) {
			if (in.hasRemaining()) {
				int outStart = out.position();
				CoderResult result = base.decode(in, out, false);
				int outEnd = out.position();
				for (int i = outStart; i < outEnd; ++i) {
					out.put(i, convertedChar(out.get(i), EUCJP, MS932));
				}
				return result;
			}
			return CoderResult.UNDERFLOW;
		}
	}

}