package com.financetracker.api.mapper;

import com.financetracker.api.request.RegisterRequest;
import com.financetracker.api.entity.User;
import com.financetracker.api.response.UserResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {

    // Map User → UserResponse (dùng khi trả response)
    // Map User → UserResponse
    @Mapping(source = "id", target = "userId")
    UserResponse toUserResponse(User user);




    // Map RegisterRequest → User (dùng khi đăng ký)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "role", ignore = true) // sẽ set thủ công trong service
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "categories", ignore = true)
    @Mapping(target = "transactions", ignore = true)
    @Mapping(target = "budgets", ignore = true)
    @Mapping(target = "notificationSettings", ignore = true)
    User toEntity(RegisterRequest dto);
}
