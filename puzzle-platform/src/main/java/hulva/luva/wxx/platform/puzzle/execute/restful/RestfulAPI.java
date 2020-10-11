package hulva.luva.wxx.platform.puzzle.execute.restful;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONObject;

import hulva.luva.wxx.platform.core.RestfulPlatform;
import hulva.luva.wxx.platform.core.exception.PluginException;
import hulva.luva.wxx.platform.puzzle.backend.controller.AbstractBaseController;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/platform")
public class RestfulAPI extends AbstractBaseController {

    @RequestMapping(value = "/sync/id/{id}", method = RequestMethod.POST)
    @ResponseBody
    public Map<String, String> syncById(@PathVariable String id, @RequestBody JSONObject jsonParam, HttpServletRequest request, HttpServletResponse response) throws Exception {
        return RestfulPlatform.onReciveData(id, request, response, jsonParam);
    }

    @RequestMapping(value = "/async/id/{id}", method = RequestMethod.POST)
    @ResponseBody
    public void asyncById(@PathVariable String id, @RequestBody JSONObject jsonParam, HttpServletRequest request, HttpServletResponse response) throws Exception {
        Thread thread = new Thread(() -> {
            try {
                RestfulPlatform.onReciveData(id, request, response, jsonParam);
            } catch (PluginException e) {
                log.error("request error by:" + id, e);
            }
        });
        thread.setDaemon(false);
        thread.start();
    }

    @RequestMapping(value = "/sync/name/{name}", method = RequestMethod.POST)
    @ResponseBody
    public Map<String, String> syncByName(@PathVariable String name, @RequestBody JSONObject jsonParam, HttpServletRequest request, HttpServletResponse response) throws Exception {
        return RestfulPlatform.onReciveDataByName(name, request, response, jsonParam);
    }

    @RequestMapping(value = "/async/name/{name}", method = RequestMethod.POST)
    @ResponseBody
    public void asyncByName(@PathVariable String name, @RequestBody JSONObject jsonParam, HttpServletRequest request, HttpServletResponse response) throws Exception {
        Thread thread = new Thread(() -> {
            try {
                RestfulPlatform.onReciveDataByName(name, request, response, jsonParam);
            } catch (PluginException e) {
                log.error("request error by:" + name, e);
            }
        });
        thread.setDaemon(false);
        thread.start();
    }
}
