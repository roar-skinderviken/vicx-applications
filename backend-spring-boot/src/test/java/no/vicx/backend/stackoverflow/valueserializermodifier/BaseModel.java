package no.vicx.backend.stackoverflow.valueserializermodifier;

abstract class BaseModel {
    public abstract long getId();

    public static class User extends BaseModel {
        private final long id;
        private final String email;

        public User(long id, String email) {
            this.id = id;
            this.email = email;
        }

        @Override
        public long getId() {
            return id;
        }

        public String getEmail() {
            return email;
        }
    }

    public static class Order extends BaseModel {
        private final long id;
        private final double total;

        public Order(long id, double total) {
            this.id = id;
            this.total = total;
        }

        @Override
        public long getId() {
            return id;
        }

        public double getTotal() {
            return total;
        }
    }
}