package com.platon.browserweb.Controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * User: dongqile
 * Date: 2018/10/23
 * Time: 9:40
 */
@RestController
@RequestMapping("/browser_api")
public class TransactionController extends BasicsController{

    private static Logger logger = LoggerFactory.getLogger(TransactionController.class);
}