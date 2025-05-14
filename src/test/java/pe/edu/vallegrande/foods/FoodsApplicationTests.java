package pe.edu.vallegrande.foods;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(properties = {"spring.r2dbc.enabled=false"})
class FoodsApplicationTests {

	@Test
	void contextLoads() {
	}

}
