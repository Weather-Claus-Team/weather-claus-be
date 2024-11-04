package com.weatherclaus.be.websocket.repository;

import com.querydsl.core.QueryResults;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.weatherclaus.be.user.entity.QUser;
import com.weatherclaus.be.websocket.entity.ChatMessage;
import com.weatherclaus.be.websocket.entity.QChatMessage;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.weatherclaus.be.user.entity.QUser.*;
import static com.weatherclaus.be.websocket.entity.QChatMessage.*;

@Repository
public class ChatMessageCustomImpl implements ChatMessageCustom {

    private EntityManager em;

    private JPAQueryFactory queryFactory;

    ChatMessageCustomImpl(EntityManager em) {
        queryFactory = new JPAQueryFactory(em);
    }




    @Override
    public Slice<ChatMessage> findAllChatMessages(Pageable pageable) {

        List<ChatMessage> content = queryFactory.select(chatMessage)
                                    .from(chatMessage)
                                    .join(chatMessage.user, user).fetchJoin()
                                    .offset(pageable.getOffset())
                                    .limit(pageable.getPageSize() + 1)
                                    .orderBy(chatMessage.sentDate.desc())
                                    .fetch();

        boolean hasNext = content.size() > pageable.getPageSize(); // 추가로 가져온 데이터가 있으면 true
        if (hasNext) {
            content.remove(content.size() - 1); // 실제 반환할 리스트에는 요청한 사이즈만 남기기
        }

        return new SliceImpl<>(content, pageable, hasNext);
    }
}
