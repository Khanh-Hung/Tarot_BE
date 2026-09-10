package tarot.application.interfaces;

import tarot.application.dto.AccountUserDto;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public interface AccountServiceClient {

    Optional<AccountUserDto> getUser(UUID userId);

    Map<UUID, AccountUserDto> getUsers(Collection<UUID> userIds);
}
