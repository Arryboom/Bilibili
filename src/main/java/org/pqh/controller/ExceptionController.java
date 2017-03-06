package main.java.org.pqh.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.servlet.http.HttpServletRequest;

@Controller
public class ExceptionController {
	@ExceptionHandler
	public String excute(HttpServletRequest req, Exception ex){
		req.setAttribute("ex", ex);
		return "jsp/error";
	}
}
