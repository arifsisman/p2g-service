//package vip.yazilim.p2g.web;
//
//import org.junit.Before;
//import org.junit.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.context.annotation.Profile;
//import org.springframework.http.MediaType;
//import org.springframework.test.context.ContextConfiguration;
//import org.springframework.test.web.servlet.MvcResult;
//import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
//import vip.yazilim.p2g.web.controller.rest.spotify.AuthorizationRest;
//import vip.yazilim.p2g.web.entity.relation.SpotifyToken;
//
//import static org.junit.Assert.assertEquals;
//import static org.junit.Assert.assertNotNull;
//
///**
// * @author mustafaarifsisman - 10.12.2019
// * @contact mustafaarifsisman@gmail.com
// */
//@ContextConfiguration("/application-test.yml")
//public class AuthTest extends AbstractTest {
//    @Override
//    @Before
//    public void setUp() {
//        super.setUp();
//    }
//
//    @Test
//    public void auth() throws Exception {
//        String uri = "/spotify/authorize";
//
//        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.get(uri).accept(MediaType.APPLICATION_JSON_VALUE)).andReturn();
//
//        int status = mvcResult.getResponse().getStatus();
//        assertEquals(200, status);
//        String content = mvcResult.getResponse().getContentAsString();
//        SpotifyToken spotifyTokens = super.mapFromJson(content, SpotifyToken.class);
//        assertNotNull(spotifyTokens);
//    }
//}
