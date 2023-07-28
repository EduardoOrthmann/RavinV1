package database;

public class Query {
    private final StringBuilder query;
    private int numberOfParameters;

    public Query() {
        this.query = new StringBuilder();
        this.numberOfParameters = 0;
    }

    public Query select(String... columns) {
        this.query.append("SELECT ");

        for (int i = 0; i < columns.length; i++) {
            this.query.append(columns[i]);

            if (i < columns.length - 1) {
                this.query.append(", ");
            }
        }

        return this;
    }

    public Query from(String table) {
        this.query.append(" FROM ").append(table);

        return this;
    }

    public Query where(String column, String operator, String value) {
        this.query.append(" WHERE ").append(column).append(" ").append(operator).append(" ").append(value);

        return this;
    }

    public Query and(String column, String operator, String value) {
        this.query.append(" AND ").append(column).append(" ").append(operator).append(" ").append(value);

        return this;
    }

    public Query or(String column, String operator, String value) {
        this.query.append(" OR ").append(column).append(" ").append(operator).append(" ").append(value);

        return this;
    }

    public Query delete() {
        this.query.append("DELETE");

        return this;
    }

    public Query insert(String table, String... columns) {
        this.query.append("INSERT INTO ").append(table).append(" (");

        for (int i = 0; i < columns.length; i++) {
            this.query.append(columns[i]);

            if (i < columns.length - 1) {
                this.query.append(", ");
            }
        }

        this.query.append(")");

        numberOfParameters = columns.length;

        return this;
    }

    public Query values(String... values) {
        this.query.append(" VALUES (");

        for (int i = 0; i < values.length; i++) {
            this.query.append(values[i]);

            if (i < values.length - 1) {
                this.query.append(", ");
            }
        }

        this.query.append(")");

        return this;
    }

    public Query autoValues() {
        this.query.append(" VALUES (");

        for (int i = 0; i < numberOfParameters; i++) {
            this.query.append("?");

            if (i < numberOfParameters - 1) {
                this.query.append(", ");
            }
        }

        this.query.append(")");

        return this;
    }


    public Query update(String table) {
        this.query.append("UPDATE ").append(table);

        return this;
    }

    public Query set(String column, String value) {
        if (this.query.toString().contains("SET")) {
            this.query.append(", ").append(column).append(" = ").append(value);
        } else {
            this.query.append(" SET ").append(column).append(" = ").append(value);
        }

        return this;
    }

    public String build() {
        return this.query.toString();
    }
}
