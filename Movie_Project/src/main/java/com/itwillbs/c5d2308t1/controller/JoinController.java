package com.itwillbs.c5d2308t1.controller;

import java.io.IOException;

import javax.servlet.http.HttpSession;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.github.scribejava.core.model.OAuth2AccessToken;
import com.itwillbs.c5d2308t1.service.JoinService;
import com.itwillbs.c5d2308t1.service.LoginService;
import com.itwillbs.c5d2308t1.vo.MemberVO;
import com.itwillbs.c5d2308t1.vo.NaverLoginBO;

@Controller
public class JoinController {
	
	// 의존성 주입받을 멤버변수 선언 시 @Autowired 어노테이션을 지정
	@Autowired
	private JoinService service;
	
	 /* NaverLoginBO */
    private NaverLoginBO naverLoginBO;
    private String apiResult = null;
    
    @Autowired
    private void setNaverLoginBO(NaverLoginBO naverLoginBO) {
        this.naverLoginBO = naverLoginBO;
    }

	
	// 회원가입(인증) 페이지로 이동
	@GetMapping("memberJoin")
	public String memberJoin(HttpSession session, Model model) {

        /* 네이버아이디로 인증 URL을 생성하기 위하여 naverLoginBO클래스의 getAuthorizationUrl메소드 호출 */
        String naverAuthUrl = naverLoginBO.getAuthorizationUrl(session);
        
        //https://nid.naver.com/oauth2.0/authorize?response_type=code&client_id=sE***************&
        //redirect_uri=http%3A%2F%2F211.63.89.90%3A8090%2Flogin_project%2Fcallback&state=e68c269c-5ba9-4c31-85da-54c16c658125
        System.out.println("네이버:" + naverAuthUrl);
        
        //네이버 
        model.addAttribute("url", naverAuthUrl);
		
		return "join/join_certification";
	}
	

	 
    //네이버 로그인 성공시 callback호출 메소드
    @RequestMapping(value = "/callback", method = { RequestMethod.GET, RequestMethod.POST })
    public String callback(MemberVO member, Model model, @RequestParam String code, @RequestParam String state, HttpSession session)
            throws IOException {
        System.out.println("여기는 callback");
        OAuth2AccessToken oauthToken;
        oauthToken = naverLoginBO.getAccessToken(session, code, state);
        //로그인 사용자 정보를 읽어온다.
        apiResult = naverLoginBO.getUserProfile(oauthToken);
        model.addAttribute("result", apiResult);
        
        JSONParser parser = new JSONParser();
        try {
            JSONObject jsonObject = (JSONObject) parser.parse(apiResult);
            JSONObject responseObj = (JSONObject) jsonObject.get("response");
            String name = (String) responseObj.get("name");
            String email = (String) responseObj.get("email");
            String phone = (String) responseObj.get("mobile");
            String birth = (String) responseObj.get("birthyear") + "-" + responseObj.get("birthday");
  
            String gender = (String) responseObj.get("gender");

            member.setMember_id(email);
            member.setMember_name(name);
            member.setMember_email(email);
            member.setMember_phone(phone);
            member.setMember_birth(birth.replace("-", "."));
            member.setMember_gender(gender);
            
            Integer dbMember = service.getMember(member);
            if(dbMember == null || dbMember.equals("") || dbMember == 0) { // 이미 가입한 아이디
            	int insertCount = service.registMember(member);
            	
            	if(insertCount > 0) { // 등록 성공
            		/* 네이버 로그인 성공 페이지 View 호출 */
            		session.setAttribute("sId", member.getMember_id());
            		return "main";
            	} else {
            		model.addAttribute("msg", "네이버 회원가입 실패!");
            		return "fail_back";
            	}	
            } else { // 새로 가입한 아이디
            	session.setAttribute("sId", member.getMember_id());
            	return "main";            	
            }
        
        } catch (ParseException e) {
            e.printStackTrace();
        }
		return "";
    }
	
	
	// 이메일 인증
	@ResponseBody
	@GetMapping("authEmail")
	public String authEmail(String member_email) {
		String auth_code = service.sendAuthMail(member_email);
		System.out.println("인증코드 : " + auth_code);
		
		return auth_code;
	}
	
	// 회원가입(동의) 페이지로 이동
	@PostMapping("memberJoinAgree")
	public String memberJoinAgree() {
		return "join/join_agree";
	}
	
	// 회원가입(폼) 페이지로 이동
	@PostMapping("memberJoinForm")
	public String memberJoinForm() {
		return "join/join_form";
	}
	
	// 회원가입 폼에 입력된 정보를 DB에 저장하고
	// 회원가입 완료 페이지로 이동
	@PostMapping("memberJoinPro")
	public String memberJoinPro(MemberVO member, Model model) {
		// 입력된 비밀번호 암호화
		BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
		String securePasswd = passwordEncoder.encode(member.getMember_passwd());
		
		// 암호화된 비밀번호를 member에 저장
		member.setMember_passwd(securePasswd);
		
		// JoinService - registMember() 메서드 호출하여 회원정보 등록 요청
		// => 파라미터 : StudentVO 객체   리턴타입 : int(insertCount)
		int insertCount = service.registMember(member);
		
		// 등록 실패 시 fail_back.jsp 페이지로 포워딩(디스패치)
		// => 포워딩 출력할 오류메세지를 "msg" 라는 속성명으로 Model 객체에 저장
		//    (현재 메서드 파라미터에 Model 타입 파라미터 변수 선언 필요)
		if(insertCount == 0) {
			model.addAttribute("msg", "회원정보 등록 실패!");
			return "fail_back";
		}
		
		return "join/join_completion";
	}
	
	// 아이디, 이메일, 휴대폰번호 중복검사 실행
	@ResponseBody
	@GetMapping("checkDup")
	public String checkDup(MemberVO member) {
		MemberVO dbMember = service.getDup(member);
		
		if(dbMember == null) { // 중복된 아이디 없음 = 사용가능
			return "false";
		} else { // 아이디 중복
			return "true";
		}
	}
	
	// 메인 페이지로 이동
	@GetMapping("main")
	public String main() {
		return "main";
	}
		
}