import java.util.regex.Pattern;

public class Messages {
    private String username = "";
    private String message = "";

    public Messages(String username, String message) {
        message = message.replace("\0","");
        message =  message.replace("\r\n","<br>");
        message =  message.replace("\n","<br>");
        if (message.length() > 103) {
            HTLMmessages(message);
        } else {
            this.message = message;
        }
        this.username = username;
    }

    private void HTLMmessages(String message) {
        String[] split = message.split(" +");
        StringBuilder finalString = new StringBuilder();
        String tmpsafe = "";
        String tmp = "";
        int index = 0;
        Pattern pattern =Pattern.compile("<br>");
        while(index < split.length){
            if(pattern.matcher(split[index]).find()){
                finalString.append(tmp).append(split[index]);
                tmp = "";
                index++;
                continue;
            }
            tmpsafe = tmp;
            tmp  = tmp.concat(split[index]+" ");
            //TODO add cases when split[] has a string with more then 100 chars
            if(tmp.length() > 100){
                tmp = "";
                finalString.append(tmpsafe).append("<br>");
                index--;
                if(index <= -1){
                    break;
                }
            }
            index++;
        }
        finalString.append(tmp);
        this.message = finalString.toString();
    }

    public String getMessage() {
        return message;
    }

    public String getUsername() {
        return username;
    }
}