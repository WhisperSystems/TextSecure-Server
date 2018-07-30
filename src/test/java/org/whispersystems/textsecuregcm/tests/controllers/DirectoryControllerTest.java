package org.whispersystems.textsecuregcm.tests.controllers;

import com.google.common.base.Optional;
import org.glassfish.jersey.test.grizzly.GrizzlyWebTestContainerFactory;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.whispersystems.dropwizard.simpleauth.AuthValueFactoryProvider;
import org.whispersystems.textsecuregcm.auth.AuthorizationToken;
import org.whispersystems.textsecuregcm.configuration.ContactDiscoveryConfiguration;
import org.whispersystems.textsecuregcm.controllers.DirectoryController;
import org.whispersystems.textsecuregcm.entities.ClientContactTokens;
import org.whispersystems.textsecuregcm.limits.RateLimiter;
import org.whispersystems.textsecuregcm.limits.RateLimiters;
import org.whispersystems.textsecuregcm.storage.DirectoryManager;
import org.whispersystems.textsecuregcm.tests.util.AuthHelper;
import org.whispersystems.textsecuregcm.util.Base64;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.LinkedList;
import java.util.List;

import io.dropwizard.testing.junit.ResourceTestRule;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyListOf;
import static org.mockito.Matchers.anyList;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class DirectoryControllerTest {

  private final RateLimiters     rateLimiters     = mock(RateLimiters.class    );
  private final RateLimiter      rateLimiter      = mock(RateLimiter.class     );
  private final DirectoryManager directoryManager = mock(DirectoryManager.class);

  private final ContactDiscoveryConfiguration cdsConfig = mock(ContactDiscoveryConfiguration.class);

  {
    try {
      when(cdsConfig.getUserAuthenticationTokenSharedSecret()).thenReturn(new byte[100]);
      when(cdsConfig.getUserAuthenticationTokenUserIdSecret()).thenReturn(new byte[100]);
    } catch (Exception e) {
      throw new AssertionError(e);
    }
  }

  @Rule
  public final ResourceTestRule resources = ResourceTestRule.builder()
                                                            .addProvider(AuthHelper.getAuthFilter())
                                                            .addProvider(new AuthValueFactoryProvider.Binder())
                                                            .setTestContainerFactory(new GrizzlyWebTestContainerFactory())
                                                            .addResource(new DirectoryController(rateLimiters,
                                                                                                 directoryManager,
                                                                                                 Optional.of(cdsConfig)))
                                                            .build();


  @Before
  public void setup() throws Exception {
    when(rateLimiters.getContactsLimiter()).thenReturn(rateLimiter);
    when(directoryManager.get(anyListOf(byte[].class))).thenAnswer(new Answer<List<byte[]>>() {
      @Override
      public List<byte[]> answer(InvocationOnMock invocationOnMock) throws Throwable {
        List<byte[]> query = (List<byte[]>) invocationOnMock.getArguments()[0];
        List<byte[]> response = new LinkedList<>(query);
        response.remove(0);
        return response;
      }
    });
  }

  @Test
  public void testGetAuthToken() {
    AuthorizationToken token =
            resources.getJerseyTest()
                     .target("/v1/directory/auth")
                     .request()
                     .header("Authorization", AuthHelper.getAuthHeader(AuthHelper.VALID_NUMBER, AuthHelper.VALID_PASSWORD))
                     .get(AuthorizationToken.class);
    assertThat(token.getUsername()).isNotEqualTo(AuthHelper.VALID_NUMBER);
    assertThat(token.getPassword()).startsWith(token.getUsername() + ":");
  }

  @Test
  public void testContactIntersection() throws Exception {
    List<String> tokens = new LinkedList<String>() {{
      add(Base64.encodeBytes("foo".getBytes()));
      add(Base64.encodeBytes("bar".getBytes()));
      add(Base64.encodeBytes("baz".getBytes()));
    }};

    List<String> expectedResponse = new LinkedList<>(tokens);
    expectedResponse.remove(0);

    Response response =
        resources.getJerseyTest()
                 .target("/v1/directory/tokens/")
                 .request()
                 .header("Authorization",
                         AuthHelper.getAuthHeader(AuthHelper.VALID_NUMBER,
                                                  AuthHelper.VALID_PASSWORD))
                 .put(Entity.entity(new ClientContactTokens(tokens), MediaType.APPLICATION_JSON_TYPE));


    assertThat(response.getStatus()).isEqualTo(200);
    assertThat(response.readEntity(ClientContactTokens.class).getContacts()).isEqualTo(expectedResponse);
  }
}
