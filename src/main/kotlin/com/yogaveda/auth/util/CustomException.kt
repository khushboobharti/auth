package com.yogaveda.auth.util

class UserDoesNotExistException : Exception()
class UserTypeException : Exception()
class EmailDoesNotExist : Exception()
class PasswordDoesNotMatch : Exception()
class CommonException(itemName: String) : Exception(itemName)
