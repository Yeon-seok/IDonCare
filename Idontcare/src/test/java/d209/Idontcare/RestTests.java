package d209.Idontcare;

import d209.Idontcare.common.APIBuilder;
import d209.Idontcare.common.dto.APIResultDto;
import d209.Idontcare.dto.TestBody;
import d209.Idontcare.dto.TestHeader;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import d209.Idontcare.common.service.APIService;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;


@SpringBootTest
public class RestTests {

  private String URL = "http://localhost:8080";

  @Autowired
  private APIService apiService;
<<<<<<< HEAD

//  @Test
//  @DisplayName("GET 테스트")
//  void getTest(){
//    Map<String, Object> headers = new HashMap<>();
//    headers.put("header_value1", 1);
//    headers.put("header_value2", 2);
//
//    Map<String, Object> queries = new HashMap<>();
//    queries.put("param1", "ㅁㄴㅇㄹ");
//    queries.put("param2", 12345);
//
//    APIResultDto<Map<String, String>, Map<String, Object>> result = null;
//    result = apiService.get(URL);
//    System.out.println(result.getBody());
//    result = apiService.get(URL, headers);
//    System.out.println(result.getBody());
//    result = apiService.get(URL, headers, queries);
//    System.out.println(result.getBody());
//    result = apiService.get(URL, null, queries);
//    System.out.println(result.getBody());
//
//    System.out.println("HEADER START");
//    TestHeader headerDto = new TestHeader("hello", 1);
//    result = apiService.get(URL, headerDto);
//    System.out.println(result.getBody());
//    result = apiService.get(URL, headerDto, queries);
//    System.out.println(result.getBody());
//
//    TestBody body = result.getBody(TestBody.class);
//    System.out.println(body.getName());
//    System.out.println(Arrays.toString(body.getAge()));
//  }
//
//  @Test
//  @DisplayName("POST 테스트")
//  void postTest(){
//    Map<String, Object> headers = new HashMap<>();
//    headers.put("header_value1", 1);
//    headers.put("header_value2", 2);
//
//    Map<String, Object> body = new HashMap<>();
//    body.put("name", "이우철");
//    body.put("age", 22);
//
//    APIResultDto<Map<String, String>, Map<String, Object>> result = null;
//    result = apiService.post(URL);
//    System.out.println(result.getBody());
//    result = apiService.post(URL, headers);
//    System.out.println(result.getBody());
//    result = apiService.post(URL, headers, body);
//    System.out.println(result.getBody());
//    result = apiService.post(URL, null, body);
//    System.out.println(result.getBody());
//    System.out.println(result.getStatus());
//  }
//
//  @Test
//  @DisplayName("Fail Test")
//  void failTest(){
//    class RequestBody{
//      String name;
//      Integer age;
//
//      public String getName() {
//        return name;
//      }
//
//      public void setName(String name) {
//        this.name = name;
//      }
//
//      public Integer getAge() {
//        return age;
//      }
//
//      public void setAge(Integer age) {
//        this.age = age;
//      }
//
//      public RequestBody(String name, Integer age){
//        this.name = name;
//        this.age = age;
//      }
//    }
//    class RequestHeader{
//      String test1;
//      Integer test2;
//
//      public RequestHeader(String test1, Integer test2) {
//        this.test1 = test1;
//        this.test2 = test2;
//      }
//
//      public String getTest1() {
//        return test1;
//      }
//
//      public void setTest1(String test1) {
//        this.test1 = test1;
//      }
//
//      public Integer getTest2() {
//        return test2;
//      }
//
//      public void setTest2(Integer test2) {
//        this.test2 = test2;
//      }
//    }
//
//    RequestBody reqBody = new RequestBody("이우철", 22);
//    RequestHeader reqHeader = new RequestHeader("헤더1", 1);
//
//    APIResultDto<Map<String, String>, Map<String, Object>> result = apiService.post(URL, reqHeader, reqBody);
//    System.out.println(result.getStatus());
//
//    result = apiService.put(URL, null, reqBody);
//    System.out.println(result.getBody());
//    result = apiService.delete(URL, null, reqBody);
//    System.out.println(result.getBody());
//  }
    @Test
    @DisplayName("GET 테스트")
    void getTest() {
      APIResultDto result = APIBuilder.build()
          .url("https://port-0-openbankapi-iciy2almk8xusg.sel5.cloudtype.app/openbanking/oauth/2.0/token")
          .method(HttpMethod.POST)
          .body(Map.of(  "phoneNumber", "01012345678",
                    "birth", "1990101",
                    "mobileSort", "SK",
                    "name", "김엄마"))
          .execute();

      System.out.println(result.getStatus());
      System.out.println(result.getHeader());
      System.out.println(result.getBody());
    }
}

=======
  
  @Test
  @DisplayName("GET 테스트")
  void getTest() {
    APIResultDto result = APIBuilder.build()
        .url("https://port-0-openbankapi-iciy2almk8xusg.sel5.cloudtype.app/openbanking/oauth/2.0/token")
        .method(HttpMethod.POST)
        .body(Map.of(  "phoneNumber", "01012345678",
                  "birth", "1990101",
                  "mobileSort", "SK",
                  "name", "김엄마"))
        .execute();
    
    System.out.println(result.getStatus());
    System.out.println(result.getHeader());
    System.out.println(result.getBody());
    
    result = APIBuilder.build()
        .url("http://localhost:3000")
        .method(HttpMethod.PUT)
        .body(Map.of("age", 1, "name", "hello"))
        .execute();
    
    System.out.println(result.getStatus());
    System.out.println(result.getHeader());
    System.out.println(result.getBody());
  }
}
>>>>>>> 4c23d5709ac78f8deda69271168cf21e4ffc96af
