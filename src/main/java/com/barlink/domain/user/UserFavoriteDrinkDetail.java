package com.barlink.domain.user;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

import javax.persistence.Entity;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;

import com.barlink.domain.drink.DrinkDetail;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * User 찜한 주류구매정보(게시글)
 * 최초생성 : 2021-07-11
 * 찜 해제시 DELETE로 삭제한다.
 * @author LeeDoyun
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
@DynamicInsert
@Data
@Table(name="user_favorite")
public class UserFavoriteDrinkDetail  implements Serializable{
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name="fav_id")
	private long favId;

	@ManyToOne(fetch = FetchType.LAZY , targetEntity = User.class)
	@JoinColumn(name = "USER_ID")
	private User user;
//	
	@ManyToOne(fetch = FetchType.LAZY , targetEntity = DrinkDetail.class)
	@JoinColumn(name="DRINK_DETAIL_ID")
	private DrinkDetail detail;
	
	@Column(name="INSERT_DATE", nullable = true, unique = false)
	private LocalDateTime insertDate;
	
	
}
