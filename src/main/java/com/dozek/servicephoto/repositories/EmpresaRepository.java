package com.dozek.servicephoto.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.dozek.servicephoto.domain.Empresa;

@Repository
public interface EmpresaRepository extends JpaRepository<Empresa,Integer> {
	
}
