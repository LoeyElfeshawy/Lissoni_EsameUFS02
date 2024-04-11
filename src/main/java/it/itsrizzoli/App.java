package it.itsrizzoli;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;
import java.util.*;

public class App {
    private static List<Pizza> pizzas = new ArrayList<>(Arrays.asList(
            new Pizza("Margherita", Arrays.asList("Pomodoro", "Mozzarella"), 10),
            new Pizza("Peperoni", Arrays.asList("Pomodoro", "Mozzarella", "Peperoni"), 12),
            new Pizza("Vegetariana", Arrays.asList("Pomodoro", "Mozzarella", "Funghi", "Peperoni", "Cipolle"), 11),
            new Pizza("Quattro Stagioni", Arrays.asList("Pomodoro", "Mozzarella", "Funghi", "Prosciutto cotto", "Carciofi", "Olive"), 13),
            new Pizza("Capricciosa", Arrays.asList("Pomodoro", "Mozzarella", "Funghi", "Prosciutto cotto", "Carciofi"), 12),
            new Pizza("Diavola", Arrays.asList("Pomodoro", "Mozzarella", "Salame piccante", "Peperoncino"), 11),
            new Pizza("Quattro Formaggi", Arrays.asList("Pomodoro", "Mozzarella", "Gorgonzola", "Fontina", "Parmigiano"), 13),
            new Pizza("Napoletana", Arrays.asList("Pomodoro", "Alici", "Origano"), 10),
            new Pizza("Marinara", Arrays.asList("Pomodoro", "Aglio", "Origano", "Olio d'oliva"), 9),
            new Pizza("Bufalina", Arrays.asList("Pomodoro", "Mozzarella di bufala", "Basilico"), 13),
            new Pizza("Calzone", Arrays.asList("Pomodoro", "Mozzarella", "Ricotta", "Prosciutto cotto"), 12),
            new Pizza("Frutti di Mare", Arrays.asList("Pomodoro", "Frutti di mare", "Aglio", "Olio d'oliva", "Prezzemolo"), 14),
            new Pizza("Tonno e Cipolla", Arrays.asList("Pomodoro", "Tonno", "Cipolla"), 11)
    ));


    public static void main(String[] args) throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(7373), 0);
        server.createContext("/", new PizzaHandler());
        server.setExecutor(null);
        server.start();
        System.out.println("Server avviato sulla porta 7373");
    }

    static class PizzaHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String requestURI = exchange.getRequestURI().toString();
            String response = "Comando non valido.";

            if (requestURI.equals("/with_tomato")) {
                response = elencoPizzeConIngrediente("Pomodoro");
            } else if (requestURI.equals("/with_cheese")) {
                response = elencoPizzeConIngrediente("Mozzarella");
            } else if (requestURI.equals("/sorted_by_price")) {
                response = "Elenco Pizze Ordinate per Prezzo:\n" + elencoPizzeOrdinatePerPrezzo();
            }

            inviaRisposta(exchange, response);
        }

        private String elencoPizzeConIngrediente(String ingrediente) {
            StringBuilder sb = new StringBuilder();
            sb.append("Pizze con ").append(ingrediente).append(":\n");
            for (Pizza pizza : pizzas) {
                if (pizza.getIngredienti().contains(ingrediente)) {
                    sb.append(pizza.getNome()).append(" - $").append(pizza.getPrezzo()).append(" - Ingredienti: ").append(String.join(", ", pizza.getIngredienti())).append("\n");
                }
            }
            return sb.toString();
        }

        private String elencoPizzeOrdinatePerPrezzo() {
            List<Pizza> pizzeOrdinate = new ArrayList<>(pizzas);
            pizzeOrdinate.sort(Comparator.comparingInt(Pizza::getPrezzo));
            StringBuilder sb = new StringBuilder();
            for (Pizza pizza : pizzeOrdinate) {
                sb.append(pizza.getNome()).append(" - $").append(pizza.getPrezzo()).append(" - Ingredienti: ").append(String.join(", ", pizza.getIngredienti())).append("\n");
            }
            return sb.toString();
        }



        private void inviaRisposta(HttpExchange exchange, String response) throws IOException {
            exchange.sendResponseHeaders(200, response.length());
            OutputStream os = exchange.getResponseBody();
            os.write(response.getBytes());
            os.close();
        }
    }

    static class Pizza {
        private String nome;
        private List<String> ingredienti;
        private int prezzo;

        public Pizza(String nome, List<String> ingredienti, int prezzo) {
            this.nome = nome;
            this.ingredienti = ingredienti;
            this.prezzo = prezzo;
        }

        public String getNome() {
            return nome;
        }

        public List<String> getIngredienti() {
            return ingredienti;
        }

        public int getPrezzo() {
            return prezzo;
        }
    }
}
