package com.pafiast.ccp.threatapiservice;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IocRepository extends JpaRepository<com.pafiast.ccp.threatapiservice.Ioc, Long> {
}