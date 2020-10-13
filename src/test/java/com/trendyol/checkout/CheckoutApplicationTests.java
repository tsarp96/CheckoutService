package com.trendyol.checkout;



import com.fasterxml.jackson.databind.ObjectMapper;
import com.trendyol.checkout.controllers.CartsController;
import com.trendyol.checkout.domain.Cart;
import com.trendyol.checkout.services.CartsService;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.HttpClientBuilder;
import org.junit.Before;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.concurrent.CompletableFuture;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.asyncDispatch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
class CheckoutApplicationTests {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private CartsService cartsService;

    @InjectMocks
    private CartsController cartsController;

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);
        this.mvc = MockMvcBuilders.standaloneSetup(cartsController).build();
    }

    @Test
    public void createCart_whenCartIsNew_ShouldReturn201WithProperLocationHeader()
            throws Exception {
        //Given
        Cart mockCart = new Cart();
        //When
        when(cartsService.createCart(any(Cart.class)))
                .thenReturn(mockCart);

        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .post("/carts")
                .accept(MediaType.APPLICATION_JSON)
                .content(convertToJson(mockCart))
                .contentType(MediaType.APPLICATION_JSON);

        MvcResult result = mvc.perform(requestBuilder).andReturn();
        MockHttpServletResponse response = result.getResponse();
        //Then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.SC_CREATED);
        assertThat(response.getHeader(HttpHeaders.LOCATION))
                .isEqualTo("http://localhost/carts/" + mockCart.getId());
    }

    public static MediaType getJsonMediaType() {
        return new MediaType(MediaType.APPLICATION_JSON.getType(),
                MediaType.APPLICATION_JSON.getSubtype(),
                Charset.forName("utf8"));
    }

    public static String convertToJson(Object o) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.writeValueAsString(o);
    }
}

