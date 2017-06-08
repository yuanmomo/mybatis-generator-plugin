package net.yuanmomo.springboot.test.controller.it;

import com.google.gson.Gson;
import net.yuanmomo.springboot.Application;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.embedded.LocalServerPort;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.net.URL;

/**
 *
 *  Base test class for Spring MVC integration test.
 *
 * Created by Hongbin.Yuan on 2017-04-04 05:22.
 */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,classes = {Application.class})
@Transactional
//@Rollback
public class BaseITTest {

    @LocalServerPort private int port;

    @Autowired protected TestRestTemplate template;

    protected static URL base;

    protected static Gson gson = new Gson();

    @Before
    public  void setUp() throws Exception {
        base = new URL("http://localhost:" + port + "/");
    }
}
