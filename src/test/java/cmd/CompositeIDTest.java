package cmd;


import org.junit.Test;
import org.kitesdk.morphline.api.AbstractMorphlineTest;
import org.kitesdk.morphline.api.Record;

public class CompositeIDTest extends AbstractMorphlineTest {

  @Test
  public void testCompositeID() throws Exception {
    morphline = createMorphline("test-morphlines/compositeID");    
    Record record = new Record();
    record.put("f1", "v1");
    record.put("f2", "v2");
    Record expected = new Record();
    expected.put("f1", "v1");
    expected.put("f2", "v2");
    expected.put("dataid", "e27f7c182977cd338c691e38aadea899");

    processAndVerifySuccess(record, expected);
  }

  

  private void processAndVerifySuccess(Record input, Record expected) {
    processAndVerifySuccess(input, expected, true);
  }

  private void processAndVerifySuccess(Record input, Record expected, boolean isSame) {
    collector.reset();
    startSession();
    assertEquals(1, collector.getNumStartEvents());
    assertTrue(morphline.process(input));
    assertEquals(expected, collector.getFirstRecord());
    if (isSame) {
      assertSame(input, collector.getFirstRecord());    
    } else {
      assertNotSame(input, collector.getFirstRecord());    
    }
  }

}
