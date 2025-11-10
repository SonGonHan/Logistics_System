package com.logistics.userauth.user.domain;

public enum UserStatus {
    ACTIVE,
    ON_DELETE // При установке такого статуса, на следующий день аккаунт удаляется (установка может быть по собственному желанию, может при увольнении)
}