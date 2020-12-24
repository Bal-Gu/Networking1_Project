public class Messages {
    private String username = "";
    private String message = "";

    public Messages(String username, String message) {
        if (message.length() > 103) {
            HTLMmessages(message);
        } else {
            this.message = message;
        }
        this.username = username;
    }

    private void HTLMmessages(String message) {
        String[] split = message.split("\\s+");
        StringBuilder finalString = new StringBuilder();
        String tmpsafe = "";
        String tmp = "";
        int index = 0;
        while(index < split.length){
            tmpsafe = tmp;
            tmp  = tmp.concat(split[index]+" ");
            //TODO add cases when split[] has a string with more then 100 chars
            if(tmp.length() > 100){
                tmp = "";
                finalString.append(tmpsafe).append("<br>");
                index--;
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