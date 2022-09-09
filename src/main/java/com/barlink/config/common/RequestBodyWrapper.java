package com.barlink.config.common;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.*;

import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.net.URLCodec;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;




public class RequestBodyWrapper extends HttpServletRequestWrapper {

	private boolean parametersParsed = false;
	private byte[] bytes;
	private final Charset encode;
	private String requestBody;
	private final Map<String,ArrayList<String>> param = new LinkedHashMap<>();
	Bytechunk tmpName = new Bytechunk();
	Bytechunk tmpValue= new Bytechunk();
	
	
	private class Bytechunk{
		private byte[] buff;
		private int start = 0;
		private int end;
		
		public void setByteChunk(byte[] b, int off, int len) {
			buff=b;
			start = off;
			end = len;
		}
		public byte[] getbytes() {
			return buff;
		}
		public int getStart () {
			return start;
		}
		public int getEnd() {
			return end;
		}
		
		public void recycle() {
			buff=null;
			start=0;
			end=0;
		}
		
	}
	
	@Override
	public String getParameter(String name) {
		if(!parametersParsed) {
			parseParameters();
		}
		ArrayList<String> values = this.param.get(name);
		if(values==null || values.size() ==0) {
			return null;
		}
		return values.get(0);
	}
	
	private void parseParameters() {
        parametersParsed = true;

        if (!("application/x-www-form-urlencoded".equalsIgnoreCase(super.getContentType()))) {
            return;
        }

        int pos = 0;
        int end = this.bytes.length;

        while (pos < end) {
            int nameStart = pos;
            int nameEnd = -1;
            int valueStart = -1;
            int valueEnd = -1;

            boolean parsingName = true;
            boolean decodeName = false;
            boolean decodeValue = false;
            boolean parameterComplete = false;

            do {
                switch (this.bytes[pos]) {
                    case '=':
                        if (parsingName) {
                            // Name finished. Value starts from next character
                            nameEnd = pos;
                            parsingName = false;
                            valueStart = ++pos;
                        } else {
                            // Equals character in value
                            pos++;
                        }
                        break;
                    case '&':
                        if (parsingName) {
                            // Name finished. No value.
                            nameEnd = pos;
                        } else {
                            // Value finished
                            valueEnd = pos;
                        }
                        parameterComplete = true;
                        pos++;
                        break;
                    case '%':
                    case '+':
                        // Decoding required
                        if (parsingName) {
                            decodeName = true;
                        } else {
                            decodeValue = true;
                        }
                        pos++;
                        break;
                    default:
                        pos++;
                        break;
                }
            } while (!parameterComplete && pos < end);

            if (pos == end) {
                if (nameEnd == -1) {
                    nameEnd = pos;
                } else if (valueStart > -1 && valueEnd == -1) {
                    valueEnd = pos;
                }
            }

            if (nameEnd <= nameStart) {
                continue;
                // ignore invalid chunk
            }

            tmpName.setByteChunk(this.bytes, nameStart, nameEnd - nameStart);
            if (valueStart >= 0) {
                tmpValue.setByteChunk(this.bytes, valueStart, valueEnd - valueStart);
            } else {
                tmpValue.setByteChunk(this.bytes, 0, 0);
            }

            try {
                String name;
                String value;

                if (decodeName) {
                    name = new String(URLCodec.decodeUrl(Arrays.copyOfRange(tmpName.getbytes(), tmpName.getStart(), tmpName.getEnd())), this.encode);
                } else {
                    name = new String(tmpName.getbytes(), tmpName.getStart(), tmpName.getEnd() - tmpName.getStart(), this.encode);
                }

                if (valueStart >= 0) {
                    if (decodeValue) {
                        value = new String(URLCodec.decodeUrl(Arrays.copyOfRange(tmpValue.getbytes(), tmpValue.getStart(), tmpValue.getEnd())), this.encode);
                    } else {
                        value = new String(tmpValue.getbytes(), tmpValue.getStart(), tmpValue.getEnd() - tmpValue.getStart(), this.encode);
                    }
                } else {
                    value = "";
                }

                if (StringUtils.isNotBlank(name)) {
                    ArrayList<String> values = this.param.get(name);
                    if (values == null) {
                        values = new ArrayList<String>(1);
                        this.param.put(name, values);
                    }
                    if (StringUtils.isNotBlank(value)) {
                        values.add(value);
                    }
                }
            } catch (DecoderException e) {
                // ignore invalid chunk
            }

            tmpName.recycle();
            tmpValue.recycle();
        }
	}
	
	
	
	
	
	public RequestBodyWrapper(HttpServletRequest request) throws IOException{
		super(request);
		
		String encoding = request.getCharacterEncoding();
		if(StringUtils.isBlank(encoding)) {
			encoding = StandardCharsets.UTF_8.name();
		}
		this.encode = Charset.forName(encoding);

		try {
			InputStream in = super.getInputStream();
			bytes = IOUtils.toByteArray(in);
			requestBody = new String(bytes);
		}catch(Exception e) {
			e.printStackTrace();
		}
	}


	@Override
	public ServletInputStream getInputStream() throws IOException {
		final ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
		return  new ServletImpl(bis);
	}

	public String getRequestBody() {
		return this.requestBody;
	}
	
	@Override
	public ServletRequest getRequest() {
		return super.getRequest();
	}
	

	class ServletImpl extends ServletInputStream{
		private InputStream is;
		public ServletImpl(InputStream bis){
			is = bis;
		}

		@Override
		public int read() throws IOException {
			return is.read();
		}

		@Override
		public int read(byte[] b) throws IOException {
			return is.read(b);
		}

		@Override
		public boolean isFinished() {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public boolean isReady() {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public void setReadListener(ReadListener listener) {
			// TODO Auto-generated method stub
			
		}
	}

}
