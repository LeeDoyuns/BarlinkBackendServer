package com.barlink.api.user.service;

import com.barlink.domain.user.User;
import com.barlink.dto.board.UserBuyListDto;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

public interface UserBuyListDrinkDetailService {
    Page<UserBuyListDto> selectUserBuyList(User user,PageRequest page);
}
