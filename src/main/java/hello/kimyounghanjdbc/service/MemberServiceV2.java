package hello.kimyounghanjdbc.service;

import hello.kimyounghanjdbc.domain.Member;
import hello.kimyounghanjdbc.repository.MemberRepositoryV1;
import hello.kimyounghanjdbc.repository.MemberRepositoryV2;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

/*
 * 트랜잭션 - 파라미터 연동, 풀을 고려한 종료
 *  */
@Slf4j
@RequiredArgsConstructor
public class MemberServiceV2 {

    private final DataSource dataSource;
    private final MemberRepositoryV2 memberRepository;

    public void accountTransfer(String fromId, String toId, int money) throws SQLException {
        Connection con = dataSource.getConnection();

        try {
            con.setAutoCommit(false);

            // 비즈니스 로직 수행
            bizLogic(con, fromId, toId, money);

            con.commit();
        } catch (Exception e) {
            con.rollback();
            throw new IllegalStateException(e);
        } finally {
            if (con != null) {
                try {
                    // auto commit = false인 채로 커넥션 풀에 돌아가지 않게 한다.
                    con.setAutoCommit(true);
                    con.close();
                } catch (Exception e) {
                    log.info("error", e);
                }
            }
        }
    }

    private void bizLogic(Connection con, String fromId, String toId, int money) throws SQLException {
        Member fromMember = memberRepository.findById(con, fromId);
        Member toMember = memberRepository.findById(con, toId);

        memberRepository.update(con, fromId, fromMember.getMoney() - money);
        validation(toMember);
        memberRepository.update(con, toId, toMember.getMoney() + money);
    }

    private void validation(Member toMember) {
        if (toMember.getMemberId().equals("ex")) {
            throw new IllegalStateException("이체 중 예외 발생");
        }
    }
}
