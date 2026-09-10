package tarot.architecture;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import tarot.application.dto.AccountUserDto;
import tarot.application.features.chat.commands.sendchatmessage.SendChatMessageHandler;
import tarot.application.features.profile.commands.claimadreward.ClaimAdRewardHandler;
import tarot.application.features.profile.commands.updatemyprofile.UpdateMyProfileHandler;
import tarot.application.features.profile.commands.uploadavatar.UploadAvatarHandler;
import tarot.application.features.profile.queries.getmyprofile.GetMyProfileHandler;
import tarot.application.features.profile.queries.getquota.GetUserQuotaHandler;
import tarot.application.features.reading.commands.createreading.CreateReadingHandler;
import tarot.application.features.reading.queries.getreadingdetail.GetReadingDetailHandler;
import tarot.application.interfaces.AccountServiceClient;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

public class ArchitectureBoundaryDecouplingTest {

    @Test
    @DisplayName("Boundary Check - No source file references identity entity package or IdentityDbConfig")
    void verifyNoIdentityPackageReferences() throws IOException {
        Path sourceRoot = Path.of("src", "main", "java");

        try (Stream<Path> paths = Files.walk(sourceRoot)) {
            List<Path> offendingFiles = paths
                    .filter(Files::isRegularFile)
                    .filter(p -> p.toString().endsWith(".java"))
                    .filter(p -> {
                        try {
                            String content = Files.readString(p);
                            return content.contains("tarot.domain.entities.identity")
                                    || content.contains("IdentityDbConfig")
                                    || content.contains("repositories.identity.UserRepository")
                                    || content.contains("spring.datasource.identity");
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    })
                    .toList();

            assertThat(offendingFiles)
                    .withFailMessage("Found files still referencing Identity database or entity: %s", offendingFiles)
                    .isEmpty();
        }
    }

    @Test
    @DisplayName("Boundary Check - All identity-dependent handlers inject AccountServiceClient")
    void verifyHandlersInjectAccountServiceClient() {
        Class<?>[] handlers = {
                GetMyProfileHandler.class,
                UpdateMyProfileHandler.class,
                UploadAvatarHandler.class,
                GetUserQuotaHandler.class,
                ClaimAdRewardHandler.class,
                CreateReadingHandler.class,
                GetReadingDetailHandler.class,
                SendChatMessageHandler.class
        };

        for (Class<?> handler : handlers) {
            boolean hasAccountServiceClient = Arrays.stream(handler.getDeclaredConstructors())
                    .flatMap(ctor -> Arrays.stream(ctor.getParameterTypes()))
                    .anyMatch(type -> type.equals(AccountServiceClient.class));

            assertThat(hasAccountServiceClient)
                    .withFailMessage("Handler %s must inject AccountServiceClient", handler.getSimpleName())
                    .isTrue();
        }
    }

    @Test
    @DisplayName("Boundary Check - AccountUserDto has no entity dependencies and is clean DTO")
    void verifyAccountUserDtoIsClean() {
        assertThat(AccountUserDto.class.isRecord()).isTrue();
        assertThat(AccountUserDto.class.getPackage().getName()).isEqualTo("tarot.application.dto");
    }
}
