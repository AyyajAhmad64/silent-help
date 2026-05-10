package com.silenthelp.silenthelp;

import com.silenthelp.silenthelp.repository.HelpRequestRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = "app.seed.enabled=true")
@AutoConfigureMockMvc
class SilenthelpApplicationTests {
	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private HelpRequestRepository helpRequestRepository;

	@Test
	void contextLoads() {
	}

	@Test
	void publicPagesRender() throws Exception {
		mockMvc.perform(get("/")).andExpect(status().isOk());
		mockMvc.perform(get("/about")).andExpect(status().isOk());
		mockMvc.perform(get("/login")).andExpect(status().isOk());
		mockMvc.perform(get("/register")).andExpect(status().isOk());
		mockMvc.perform(get("/requests")).andExpect(status().is3xxRedirection());
	}

	@Test
	@WithMockUser(username = "student", roles = "STUDENT")
	void studentPagesRender() throws Exception {
		Long requestId = helpRequestRepository.findAll().getFirst().getId();
		mockMvc.perform(get("/dashboard")).andExpect(status().isOk());
		mockMvc.perform(get("/requests")).andExpect(status().isOk());
		mockMvc.perform(get("/requests/new")).andExpect(status().isOk());
		mockMvc.perform(get("/requests/" + requestId)).andExpect(status().isOk());
		mockMvc.perform(get("/my-requests")).andExpect(status().isOk());
		mockMvc.perform(get("/my-responses")).andExpect(status().isOk());
		mockMvc.perform(get("/notifications")).andExpect(status().isOk());
		mockMvc.perform(get("/profile")).andExpect(status().isOk());
	}

	@Test
	@WithMockUser(username = "admin", roles = "ADMIN")
	void adminPagesRender() throws Exception {
		mockMvc.perform(get("/admin")).andExpect(status().isOk());
		mockMvc.perform(get("/profile")).andExpect(status().isOk());
		mockMvc.perform(get("/admin/users")).andExpect(status().isOk());
		mockMvc.perform(get("/admin/deleted-accounts")).andExpect(status().isOk());
		mockMvc.perform(get("/admin/posts")).andExpect(status().isOk());
		mockMvc.perform(get("/admin/categories")).andExpect(status().isOk());
		mockMvc.perform(get("/admin/reports")).andExpect(status().isOk());
	}

}
