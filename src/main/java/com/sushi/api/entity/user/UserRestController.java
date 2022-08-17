package com.sushi.api.entity.user;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Tag(name = "Users", description = "User Operations")
@RestController
@RequestMapping("/users")
public class UserRestController {

}
