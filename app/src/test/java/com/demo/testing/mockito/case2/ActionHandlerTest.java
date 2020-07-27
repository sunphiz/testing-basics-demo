package com.demo.testing.mockito.case2;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ActionHandlerTest {

    // SUT
    ActionHandler actionHandler;

    @Mock
    Service service;

    @Mock
    Response response;

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);

        actionHandler = new ActionHandler(service);
    }

    @Test
    public void getValue_should_return_valid_string_if_doRequest_succeed() {
        // Given
        when(response.isSuccessful()).thenReturn(true);
        when(response.getData()).thenReturn("valid result");

        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Callback callback = invocation.getArgument(1);
                callback.reply(response);
                return null;
            }
//        }).when(service).getResponse(anyString(), isA(Callback.class));
        }).when(service).getResponse(eq("request"), isA(Callback.class));

        // When
        actionHandler.doRequest("request");

        // Then
        assertThat(actionHandler.getValue(), is("valid result"));
    }

//    @Captor
    ArgumentCaptor<Callback> mCaptor;

    @Test
    public void getValue_should_return_null_if_doRequest_failed() {
        // Given
        when(response.isSuccessful()).thenReturn(false);
        mCaptor = ArgumentCaptor.forClass(Callback.class);

        // When
        actionHandler.doRequest("failed request");

        // Then
        verify(service).getResponse(anyString(), mCaptor.capture());
        mCaptor.getValue().reply(response);

        assertThat(actionHandler.getValue(), is(nullValue()));
    }
}