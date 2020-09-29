package automail;

public class MailTube extends Storage {

  private static final int CAPACITY = 1;
  private static final int BOOT_TIME = 0;
  private static final Class<MailItem> ACCEPTS = MailItem.class;

  public MailTube() {
    super(CAPACITY, BOOT_TIME, ACCEPTS);
  }
}
