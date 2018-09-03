package com.yq.controller;

import com.alibaba.fastjson.JSONObject;
import com.ecwid.consul.v1.health.model.HealthService;
import com.orbitz.consul.model.health.ServiceHealth;
import com.yq.service.IConsulService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;

@Api("consul API")
@RestController
@RequestMapping("/consul1")
public class ConsulController {
    @Autowired
    @Qualifier("orbitzConsulServiceImpl")
    private IConsulService consulService;

    @Autowired
    private DiscoveryClient discoveryClient;

    @ApiOperation("register service")
    @RequestMapping(value="/regSvc/{svcName}/{svcId}",method=RequestMethod.POST)
    public void registerService(@PathVariable("svcName") String svcName,
                                @PathVariable("svcId") String svcId) {
        consulService.registerService(svcName, svcId);
    }

    @ApiOperation("discover service")
    @RequestMapping(value="/disSvc1/{svcName}",method=RequestMethod.GET)
    public String discoverService(@PathVariable("svcName") String svcName) {
        List<ServiceHealth> list = consulService.findServiceHealthy(svcName);

        JSONObject jsonObj = new JSONObject();
        jsonObj.put("currentTime", LocalDateTime.now().toString());
        jsonObj.put("list", list);
        jsonObj.put("size", list.size());
        return jsonObj.toJSONString();
    }

    @ApiOperation("discover service by discoveryClient")
    @RequestMapping(value="/disSvc2/{svcName}",method=RequestMethod.GET)
    public String discoverServiceByClient(@PathVariable("svcName") String svcName) {
        List<ServiceInstance> list = discoveryClient.getInstances(svcName);

        JSONObject jsonObj = new JSONObject();
        jsonObj.put("currentTime", LocalDateTime.now().toString());
        jsonObj.put("list", list);
        jsonObj.put("size", list.size());
        return jsonObj.toJSONString();
    }


    @ApiOperation("store KV")
    @RequestMapping(value="/kv/{key}/{value}",method=RequestMethod.POST)
    public void storeKV(@PathVariable("key") String key,
                        @PathVariable("value") String value) {
        consulService.storeKV(key, value);
    }

    @ApiOperation("get KV")
    @RequestMapping(value="/kv/{key}",method=RequestMethod.GET)
    public String getKV(@PathVariable("key") String key) {
        return consulService.getKV(key);
    }

    @ApiOperation("获取同一个DC中的所有server节点")
    @RequestMapping(value="/raftpeers",method=RequestMethod.GET)
    public List<String> findRaftPeers() {
        return consulService.findRaftPeers();
    }

    @ApiOperation("获取leader")
    @RequestMapping(value="/leader",method=RequestMethod.GET)
    public String leader() {
        return consulService.findRaftLeader();
    }
}
