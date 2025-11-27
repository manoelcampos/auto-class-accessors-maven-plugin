package io.github.manoelcampos.accessors.sample;

import java.util.function.Supplier;

public class Main {
    private static long lastId = 1;

    public static void main(final String[] args) {
        final var product1 = new Product();
        product1.id = 1L;
        product1.available = true;
        product1.name = "   TV   ";
        product1.model = "xy123";

        System.out.printf("Product id: %d name: %s model: %s available: %s%n%n", product1.id, product1.name, product1.model, product1.available);


        final var product2 = newProduct(() -> {
            final var p = new Product();
            p.name = "Smartphone      ";
            p.brand = "new";
            p.model = "pro-x";
            return p;
        });

        System.out.println(product2);
    }

    /**
     * Crates a new {@link Product} instance using the given supplier and assignes an auto-increment ID.
     * @param supplier the {@link Supplier} to get a new Product.
     * @return the new Product instance with an assigned ID.
     */
    private static Product newProduct(Supplier<Product> supplier) {
        final var product = supplier.get();
        product.id = ++lastId;
        return product;
    }
}
