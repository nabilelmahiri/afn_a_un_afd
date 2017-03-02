/**
 *
 * @author EL Mahiri Nabil
 */


package nfdtoafd.module;

import java.util.*;

public class Automaton {


    private String Nombre;
    private int Etatsnum;
    private int EtatInitial;
    private int EtatActuel;
    private TreeSet<String> Alphabet;
    private TreeSet<Integer> etatFinal;
    private TreeSet<Integer>[][] TableTransitions;

    public Automaton() {
        super();
        Alphabet = new TreeSet<String>();
        etatFinal = new TreeSet<Integer>();


    }

    public Automaton(String nombre, int nEtats, TreeSet<String> alphabet, int q0,
            TreeSet<Integer> qend, TreeSet<Integer>[][] tableTransitions) {
        super();
        Nombre = nombre;
        this.Etatsnum = nEtats;
        Alphabet = alphabet;
        this.EtatInitial = q0;
        etatFinal = qend;
        TableTransitions = tableTransitions;
    }

    public String getNombre() {
        return Nombre;
    }

    public void setNombre(String nombre) {
        Nombre = nombre;
    }

    public int getEtatsnum() {
        return Etatsnum;
    }

    public void setEtatsnum(int nEtats) {
        this.Etatsnum = nEtats;
    }

    public TreeSet<String> getAlphabet() {
        return Alphabet;
    }

    public void setAlphabet(TreeSet<String> alphabet) {
        Alphabet = alphabet;
    }

    public int getEtatInitial() {
        return EtatInitial;
    }

    public void setEtatInitial(int q0) {
        this.EtatInitial = q0;
    }

    public TreeSet<Integer> getetatFinal() {
        return etatFinal;
    }

    public void setetatFinal(TreeSet<Integer> qend) {
        etatFinal = qend;
    }

    public TreeSet<Integer>[][] getTableTransitions() {
        return TableTransitions;
    }

    public void setTableTransitions(TreeSet<Integer>[][] tableTransitions) {
        TableTransitions = tableTransitions;
    }

    public void addEtatFinal(int q) {
        etatFinal.add(q);
    }

    public int getEtatActuel() {
        return EtatActuel;
    }

    @SuppressWarnings("unchecked")
    public void addLettreAlphabet(String letra) {
        Alphabet.add(letra);
        TableTransitions = new TreeSet[Etatsnum][Alphabet.size()];
        iniciarTableTransitions();
    }

    private void iniciarTableTransitions() {
        for (int x = 0; x < Etatsnum; x++) {
            for (int y = 0; y < Alphabet.size(); y++) {
                TableTransitions[x][y] = new TreeSet<Integer>();
            }
        }
    }

    public void addTransicion(int q0, String e, int q1) {
        Vector<String> a = new Vector<String>();
        a.addAll(Alphabet);
        TableTransitions[q0][a.indexOf(e)].add(q1);
    }

    public boolean analizarPalabra(String palabra) {

        EtatActuel = EtatInitial;
        String[] letras = palabra.split("");

        for (String l : letras) {
            if (!l.equals("")) {
                EtatActuel = funcion(EtatActuel, l);
                if (EtatActuel == -1) {
                    return false;
                }
            }
        }
        if (etatFinal.contains(EtatActuel)) {
            return true;
        }

        return false;
    }

    private int funcion(int estadoActual, String e) {
        Vector<String> a = new Vector<String>();
        a.addAll(Alphabet);
        if (TableTransitions[estadoActual][a.indexOf(e)].isEmpty()) {
            return -1;
        } else {
            return TableTransitions[estadoActual][a.indexOf(e)].first();
        }

    }
}

