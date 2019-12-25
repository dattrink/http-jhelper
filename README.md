# jhelper
Java Http Helper

## Usage

Http Utility implements Antarix/MultiPartUtility
https://gist.github.com/Antarix/a36faeaff3092b1fd977

### GetUtility

``` java
List<Map<String, Object>> result = null;
SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
Map<String, String> headers = new HashMap<String, String>();
Map<String, String> params = new HashMap<String, String>();
params.put("type", type);
params.put("id", "2");
params.put("date", formatter.format(new Date()));
GetUtility getUtil = new GetUtility("https://domain.com/api/v1/user", params, headers);
try {
    int responseCode = getUtil.connect();
    String payload = getUtil.getPayload();
    if (responseCode == HttpStatus.OK_200 && payload != null) { //response success
        result = this.parseListMap(payload);
    } else {
        throw new HttpException();
    }
} catch (HttpException e) {
    throw e;
} catch(java.net.ConnectException ex) {
    throw new HttpException("Timeout");
} catch (Exception ex) {
    ex.printStackTrace();
}
```

### PostUtility

``` java
LinkedHashMap<String, String> params = new HashMap<String, String>();
params.put("id", "2");
params.put("date", formatter.format(new Date()));
try {
    PostUtility postUtil = new PostUtility("https://domain.com/api/v2/user", params, null);
    int status = postUtil.connect();
    if (status == HttpStatus.OK_200) {
        String payload = postUtil.getPayload();
    } else {
        throw new Exception();
    }
} catch (IOException e) {
    _log.error("{}", e);
    throw new Exception();
}
```
