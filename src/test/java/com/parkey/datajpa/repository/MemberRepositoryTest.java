package com.parkey.datajpa.repository;

import com.parkey.datajpa.entity.Member;
import com.parkey.datajpa.entity.Team;
import org.hibernate.Hibernate;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
//@Rollback(false)
class MemberRepositoryTest {

    @PersistenceContext
    private EntityManager em;

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    TeamRepository teamRepository;

    @Test
    public void usernameTest() {
        Member a = new Member("a", 10);
        Member b = new Member("b", 20);
        em.persist(a);
        em.persist(b);

        List<Member> result = memberRepository.findByUsernameAndAgeGreaterThan("b", 15);
        assertThat(result.get(0).getUsername()).isEqualTo("b");

    }
    @Test
    public void bulkUpdate() throws Exception {
        //given
        memberRepository.save(new Member("member1", 10));
        memberRepository.save(new Member("member2", 19));
        memberRepository.save(new Member("member3", 20));
        memberRepository.save(new Member("member4", 21));
        memberRepository.save(new Member("member5", 40));

        //when
        int resultCount = memberRepository.bulkAgePlus(20);

        List<Member> all = memberRepository.findAll();
        all.forEach(System.out::println);

        //then
        assertThat(resultCount).isEqualTo(3);
    }

    @Test
    public void findMemberLazy() throws Exception {
        //member1 -> teamA
        //member2 -> teamB

        Team teamA = new Team("teamA");
        Team teamB = new Team("teamB");
        teamRepository.save(teamA);
        teamRepository.save(teamB);
        Member member1 = new Member("member1", 10, teamA);
        memberRepository.save(member1);
        Member member2 = new Member("member2", 20, teamB);
        memberRepository.save(member2);
        em.flush();
        em.clear();
        //when
        List<Member> members = memberRepository.findAll();

        System.out.println("is lazy1 :" + Hibernate.isInitialized(member1.getTeam()));
        System.out.println("is lazy2 :" + Hibernate.isInitialized(member2.getTeam()));

        //then
        members.forEach(member -> member.getTeam().getName());


    }

}