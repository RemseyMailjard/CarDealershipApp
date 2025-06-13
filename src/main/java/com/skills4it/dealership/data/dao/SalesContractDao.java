package com.skills4it.dealership.data.dao;

import com.skills4it.dealership.model.SalesContract;
import java.util.List;

public interface SalesContractDao {
    SalesContract create(SalesContract contract);
    List<SalesContract> getAll();
}