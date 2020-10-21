package com.trendyol.checkout;



import com.fasterxml.jackson.databind.ObjectMapper;
import com.trendyol.checkout.controllers.CartsController;
import com.trendyol.checkout.domain.Cart;
import com.trendyol.checkout.domain.Product;
import com.trendyol.checkout.exceptions.ItemAlreadyExistInCartException;
import com.trendyol.checkout.services.CartsService;
import com.trendyol.checkout.services.RestService;
import org.apache.http.HttpException;
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
import org.springframework.http.ResponseEntity;
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
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mock;
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
    @MockBean
    private RestService restService;
    @InjectMocks
    private CartsController cartsController;
    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);
        this.mvc = MockMvcBuilders.standaloneSetup(cartsController).build();
    }

    @Test
    public void createCart_whenCartIsNew_ShouldReturn201WithProperLocationHeader() throws Exception {
        //Given
        Cart mockCart = new Cart();
        //When
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

    @Test
    public void addItem_WhenItemIdIsNotExist_ShouldReturn204() throws Exception {
        //Given
        String itemId = "646549846";
        //When
        when(restService.getProductByIdAsObject(itemId)).thenReturn(null);
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .post("/carts/123123213/items/1231231")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON);

        MvcResult result = mvc.perform(requestBuilder).andReturn();
        MockHttpServletResponse response = result.getResponse();
        //Then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.SC_NO_CONTENT);
    }

    @Test
    public void addItem_whenStockIsNotAvailable_ShouldReturn204() throws Exception {
        //Given
        String cartId = "987987";
        String itemId = "1231231";
        Product product = new Product();
        product.setId(itemId);
        //When
        when(restService.getProductByIdAsObject(itemId)).thenReturn(product);
        when(restService.isStockAvailableForProductId(product.getId())).thenReturn(false);
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .post("/carts/"+ cartId + "/items/" + itemId )
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON);

        MvcResult result = mvc.perform(requestBuilder).andReturn();
        MockHttpServletResponse response = result.getResponse();
        //Then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.SC_NO_CONTENT);
        assertThat(response.getContentAsString()).isEqualTo("No Available Stock for Product !");
    }

    @Test
    public void addItem_whenStockIsAvailable_ShouldReturn200() throws Exception {
        //Given
        String cartId = "987987";
        String itemId = "1231231";
        Product product = new Product();
        product.setId(itemId);
        //When
        when(restService.getProductByIdAsObject(itemId)).thenReturn(product);
        when(restService.isStockAvailableForProductId(product.getId())).thenReturn(true);
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .post("/carts/"+ cartId + "/items/" + itemId)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON);

        MvcResult result = mvc.perform(requestBuilder).andReturn();
        MockHttpServletResponse response = result.getResponse();
        //Then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.SC_OK);
    }

    @Test
    public void getCartById_whenCartIsNull_ShouldReturn404() throws Exception {
        //Given
        String cartId = "987987";
        //When
        when(cartsService.getCartById(cartId)).thenReturn(null);
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .get("/carts/"+ cartId)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON);

        MvcResult result = mvc.perform(requestBuilder).andReturn();
        MockHttpServletResponse response = result.getResponse();
        //Then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.SC_NOT_FOUND);
    }

    @Test
    public void getCartById_whenEverythingIsOK_ShouldReturn200() throws Exception {
        //Given
        String cartId = "987987";
        Cart cart = new Cart();
        //When
        when(cartsService.getCartById(cartId)).thenReturn(cart);
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .get("/carts/"+ cartId)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON);

        MvcResult result = mvc.perform(requestBuilder).andReturn();
        MockHttpServletResponse response = result.getResponse();
        //Then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.SC_OK);
    }

    @Test
    public void getCartByUserId_whenUserIDIsNotValid_ShouldReturn404() throws Exception{
        //Given
        String userID = "444";
        //When
        when(cartsService.getCartByUserId(userID)).thenReturn(null);
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .get("/carts?userId="+ userID )
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON);

        MvcResult result = mvc.perform(requestBuilder).andReturn();
        MockHttpServletResponse response = result.getResponse();
        //Then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.SC_NOT_FOUND);
    }

    @Test
    public void getCartByUserId_whenEverythingIsOK_ShouldReturn200() throws Exception{
        //Given
        String userID = "444";
        Cart cart = new Cart();
        //When
        when(cartsService.getCartByUserId(userID)).thenReturn(cart);
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .get("/carts?userId="+ userID )
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON);

        MvcResult result = mvc.perform(requestBuilder).andReturn();
        MockHttpServletResponse response = result.getResponse();
        //Then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.SC_OK);
    }

    @Test
    public void deleteItem_whenItemIdIsNotExist_ShouldReturn204() throws Exception {
        //Given
        String itemId = "123456789";
        //When
        when(restService.getProductByIdAsObject(itemId)).thenReturn(null);
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .delete("/carts/123123213/items/1231231")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON);

        MvcResult result = mvc.perform(requestBuilder).andReturn();
        MockHttpServletResponse response = result.getResponse();
        //Then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.SC_NO_CONTENT);
    }
    @Test
    public void deleteItem_whenItemIsInCart_ShouldReturn200() throws Exception {
        //Given
        String cartId = "987987";
        String itemId = "1231231";
        Product product = new Product();
        Cart cart = new Cart();
        product.setId(itemId);
        cart.setId(cartId);
        //When
        when(restService.getProductByIdAsObject(itemId)).thenReturn(product);
        when(restService.getCartByIdAsObject(cartId)).thenReturn(cart);
        when(restService.isProductInCart(cart, product)).thenReturn(true);
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .delete("/carts/"+ cartId + "/items/" + itemId)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON);

        MvcResult result = mvc.perform(requestBuilder).andReturn();
        MockHttpServletResponse response = result.getResponse();
        //Then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.SC_OK);

    }
    @Test
    public void deleteItem_whenItemIsNotInCart_ShouldReturn204() throws Exception {
        //Given
        String cartId = "987987";
        String itemId = "1231231";
        Product product = new Product();
        Cart cart = new Cart();
        product.setId(itemId);
        cart.setId(cartId);
        //When
        when(restService.getProductByIdAsObject(itemId)).thenReturn(product);
        when(restService.getCartByIdAsObject(cartId)).thenReturn(cart);
        when(restService.isProductInCart(cart, product)).thenReturn(false);
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .delete("/carts/"+ cartId + "/items/" + itemId)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON);

        MvcResult result = mvc.perform(requestBuilder).andReturn();
        MockHttpServletResponse response = result.getResponse();
        //Then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.SC_NO_CONTENT);
        assertThat(response.getContentAsString()).isEqualTo("Product is not in cart!");
    }

    @Test
    public void addItem_whenBothItemsAreIdentical_ShouldThrowItemAlreadyExistInCartException() throws ItemAlreadyExistInCartException, Exception {
        //Given
        Cart sut = new Cart();
        Product product = new Product();
        cartsService.addItemToCart(sut.getId(), product);

        //When
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .post("/carts/"+ sut.getId() + "/items/" + product.getId())
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON);

        MvcResult result = mvc.perform(requestBuilder).andReturn();
        MockHttpServletResponse response = result.getResponse();

        //Then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.SC_NO_CONTENT);

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

