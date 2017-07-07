package chenxu.morphlines.cmd;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.kitesdk.morphline.api.Command;
import org.kitesdk.morphline.api.CommandBuilder;
import org.kitesdk.morphline.api.MorphlineCompilationException;
import org.kitesdk.morphline.api.MorphlineContext;
import org.kitesdk.morphline.api.Record;
import org.kitesdk.morphline.base.AbstractCommand;

import com.typesafe.config.Config;

public final class CompositeIDBuilder implements CommandBuilder {

  @Override
  public Collection<String> getNames() {
    return Collections.singletonList("compositeID");
  }

  @Override
  public Command build(Config config, Command parent, Command child, MorphlineContext context) {
    try {
      return new CompositeID(this, config, parent, child, context);
    } catch (IOException e) {
      throw new MorphlineCompilationException("Cannot compile", config, e);
    }
  }
  
  
  ///////////////////////////////////////////////////////////////////////////////
  // Nested classes:
  ///////////////////////////////////////////////////////////////////////////////
  private static final class CompositeID extends AbstractCommand {
    

    private List<String> inputFields;
	private String dataid;
	private StringBuffer sb = new StringBuffer();
	private MessageDigest md;

	public CompositeID(CommandBuilder builder, Config config, Command parent, Command child, MorphlineContext context) 
        throws IOException {
      
      super(builder, config, parent, child, context);
      inputFields = getConfigs().getStringList(config, "inputFields", Collections.<String>emptyList()); 
      dataid = getConfigs().getString(config,"idFieldName","dataid");

      validateArguments();
      try {
		md = MessageDigest.getInstance("MD5");
	} catch (NoSuchAlgorithmException e) {
		//ignore
	}

    }

	@Override
	protected boolean doProcess(Record record) {
		
		for(String field : inputFields) {
			sb.append(record.getFirstValue(field));
		}
		//计算拼接完的MD5值
		md.update(sb.toString().getBytes());
		byte[] digest = md.digest();
		sb.setLength(0);
		for(int offset=0; offset<digest.length; offset++) {
			String hex = Integer.toHexString(0XFF & digest[offset]);
			if(hex.length()==1)
				sb.append('0');
			sb.append(hex);
		}
		//将计算出来的MD5值，添加到指定字段上
		record.put(dataid, sb.toString());
		sb.setLength(0);
		return super.doProcess(record);
	}
    
  }  
}