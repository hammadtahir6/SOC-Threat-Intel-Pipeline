package com.pafiast.ccp.extractionservice;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IocRepository extends JpaRepository<Ioc, Long> {
}