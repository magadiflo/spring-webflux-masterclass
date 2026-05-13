package dev.magadiflo.customer.app.dto;

import java.util.List;

public record CustomerInformation(Long id,
                                  String name,
                                  Integer balance,
                                  List<Holding> holdings) {
}
