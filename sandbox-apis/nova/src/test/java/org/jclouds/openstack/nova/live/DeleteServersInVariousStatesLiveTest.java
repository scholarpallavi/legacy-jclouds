/**
 *
 * Copyright (C) 2010 Cloud Conscious, LLC. <info@cloudconscious.com>
 *
 * ====================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ====================================================================
 */

package org.jclouds.openstack.nova.live;

import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableMap;
import org.jclouds.http.HttpResponseException;
import org.jclouds.net.IPSocket;
import org.jclouds.openstack.nova.NovaClient;
import org.jclouds.openstack.nova.domain.Server;
import org.jclouds.openstack.nova.domain.ServerStatus;
import org.jclouds.ssh.SshClient;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.Test;

import java.util.Map;

import static org.jclouds.openstack.nova.options.CreateServerOptions.Builder.withFile;

/**
 * @author Victor Galkin
 */

@Test(groups = "live", sequential = true)
public class DeleteServersInVariousStatesLiveTest {

   protected NovaClient client;
   protected SshClient.Factory sshFactory;
   private Predicate<IPSocket> socketTester;
   protected String provider = "nova";
   protected String identity;
   protected String credential;
   protected String endpoint;
   protected String apiversion;

   Map<String, String> metadata = ImmutableMap.of("jclouds", "rackspace");
   Server server = null;

   @Test(expectedExceptions = HttpResponseException.class, expectedExceptionsMessageRegExp = ".*Internal Server Error.*")
   public void testCreateServerWithUnknownImage() throws Exception {
      try {
         server = client.createServer("serverName", String.valueOf(88888888), "1", withFile("/etc/jclouds.txt",
               "rackspace".getBytes()).withMetadata(metadata));
      } catch (HttpResponseException e) {
         throw e;
      }
   }

   @Test(expectedExceptions = HttpResponseException.class, expectedExceptionsMessageRegExp = ".*Internal Server Error.*")
   public void testCreateServerWithUnknownFlavor() throws Exception {
      try {
         server = client.createServer("serverName", String.valueOf(13), "88888888", withFile("/etc/jclouds.txt",
               "rackspace".getBytes()).withMetadata(metadata));
      } catch (HttpResponseException e) {
         throw e;
      }
   }

   @AfterMethod
   public void after() {
      if (server != null) client.deleteServer(server.getId());
   }

   @Test(enabled = true)
   public void testCreateServer() throws Exception {
//      String imageRef = client.getImage(13).getURI().toASCIIString();
//      String flavorRef = client.getFlavor(1).getURI().toASCIIString();
//      String serverName = serverPrefix + "createserver" + new SecureRandom().nextInt();
//      Server server = client.createServer(serverName, imageRef, flavorRef, withFile("/etc/jclouds.txt",
//            "rackspace".getBytes()).withMetadata(metadata));
//
//      assertNotNull(server.getAdminPass());
//      assertEquals(server.getStatus(), ServerStatus.BUILD);
//      serverId = server.getId();
//      adminPass = server.getAdminPass();
//      blockUntilServerActive(serverId);
//      client.getServer(serverId).getAddresses().getPublicAddresses().iterator().next().getAddress();
   }

   private void blockUntilServerActive(int serverId) throws InterruptedException {
      Server currentDetails;
      for (currentDetails = client.getServer(serverId); currentDetails.getStatus() != ServerStatus.ACTIVE; currentDetails = client
            .getServer(serverId)) {
         System.out.printf("blocking on status active%n%s%n", currentDetails);
         Thread.sleep(5 * 1000);
      }
   }
}
