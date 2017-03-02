/**
 *
 * Réalisé par : 
•	EL Mahiri Nabil
•	Hicham Mamouni Alaoui
Encadré par :
•	Mr. BADDI YOUSSEF
 */

package nfdtoafd.module;

import java.util.Stack;
import java.util.TreeSet;
import java.util.Vector;


import javax.swing.JOptionPane;

public class Transformer {

    private String nombre;
    private int etatsnum;
    private int etatinitial;
    private TreeSet<String> alphabet;
    private TreeSet<Integer> etatfinal;
    private TreeSet<Integer>[][] tabtrans;

    public Automaton minimiser(Automaton automaton) {

        nombre = automaton.getNombre();
        etatsnum = automaton.getEtatsnum();
        alphabet = automaton.getAlphabet();
        etatinitial = automaton.getEtatInitial();
        etatfinal = automaton.getetatFinal();
        tabtrans = automaton.getTableTransitions();

        if (alphabet.contains("E")) {
            JOptionPane.showMessageDialog(null, "Retrait des transitions vides");

            supprimerTransitionsvides();
            JOptionPane.showMessageDialog(null, "les transitions vides sont supprimées");

        } else {
            JOptionPane.showMessageDialog(null, "transitions pas vides");


        }
        if (nonDeterminist()) {
            JOptionPane.showMessageDialog(null, "la suppression d'indéterminisme");

            supprimerindeterminisme();
            JOptionPane.showMessageDialog(null, "indéterminisme enlevé");

        } else {
            JOptionPane.showMessageDialog(null, "Il est déterministe");

        }

        while (!verificationMinimale()) {
            minimiser();
        }
        JOptionPane.showMessageDialog(null, "Elle est minime");

        return new Automaton(nombre, etatsnum, alphabet, etatinitial, etatfinal, tabtrans);

    }

    private void supprimerindeterminisme() {

        Vector<TreeSet> nuevosEstados = new Vector<TreeSet>();
        TreeSet<Integer> ts;
        TreeSet<Integer> ts2;

        TreeSet<Integer> c = new TreeSet<Integer>();
        c.add(0);
        nuevosEstados.add(c);

        for (String s : alphabet) {
            for (int cont = 0; cont < etatsnum; cont++) {
                ts = obtenirTransition(cont, s);
                if (ts.size() != 0 && !nuevosEstados.contains(ts)) {
                    nuevosEstados.add(ts);
                }
            }
        }
        Vector<TreeSet> temporal = (Vector<TreeSet>) nuevosEstados.clone();

        for (TreeSet<Integer> t : temporal) {
            ts2 = new TreeSet<Integer>();
            for (String s : alphabet) {
                for (Integer i : t) {
                    ts2.addAll(obtenirTransition(i, s));
                }
                if (ts2.size() != 0 && !nuevosEstados.contains(ts2)) {
                    nuevosEstados.add(ts2);
                }
            }
        }

        TreeSet<Integer>[][] tablaaux = new TreeSet[nuevosEstados.size()][alphabet.size()];


        TreeSet<Integer> tranO, tran;
        for (String s : alphabet) {
            for (TreeSet<Integer> t : nuevosEstados) {
                tranO = new TreeSet<Integer>();
                tran = new TreeSet<Integer>();
                for (Integer i : t) {
                    tranO.addAll(obtenirTransition(i, s));
                }

                ///-nuevo
                if (nuevosEstados.indexOf(tranO) != -1) {
                    tran.add(nuevosEstados.indexOf(tranO));
                }

                Vector<String> a = new Vector<String>();
                a.addAll(alphabet);
                tablaaux[nuevosEstados.indexOf(t)][a.indexOf(s)] = tran;
            }
        }

        TreeSet<Integer> finales = new TreeSet<Integer>();

        for (TreeSet<Integer> t : nuevosEstados) {
            for (Integer i : etatfinal) {
                if (t.contains(i)) {
                    finales.add(nuevosEstados.indexOf(t));
                }
            }
        }

        etatsnum = nuevosEstados.size();
        etatfinal = finales;
        tabtrans = tablaaux;
        System.out.println();
    }

    private boolean nonDeterminist() {
        boolean b = false;
        TreeSet<Integer> ts = new TreeSet<Integer>();
        for (String s : alphabet) {
            for (int cont = 0; cont < etatsnum; cont++) {
                ts = obtenirTransition(cont, s);
                if (ts != null && ts.size() > 1) {
                    b = true;
                }
            }
        }
        return b;
    }

    private void supprimerTransitionsvides() {
        TreeSet<Integer> tran;
        TreeSet<Integer> clau;
        TreeSet<Integer> clau2;

        TreeSet<String> alphabetTemp = (TreeSet<String>) alphabet.clone();
        alphabetTemp.remove("E");
        TreeSet<Integer>[][] tablatransicionesTemp = new TreeSet[etatsnum][alphabetTemp.size()];

        for (int a = 0; a < alphabetTemp.size(); a++) {
            for (int q = 0; q < etatsnum; q++) {
                tablatransicionesTemp[q][a] = new TreeSet<Integer>();
            }

        }

        for (String s : alphabet) {
            if (!s.equals("E")) {
                for (int cont = 0; cont < etatsnum; cont++) {
                    //System.out.print(cont + " "+s+" -");

                    tran = new TreeSet<Integer>();
                    clau = cerrarVacias(cont);
                    clau2 = new TreeSet<Integer>();
                    for (Integer i : clau) {
                        tran.addAll(obtenirTransition(i.intValue(), s));
                    }
                    for (Integer i : tran) {
                        clau2.addAll(cerrarVacias(i.intValue()));

                        Vector<String> a = new Vector<String>();
                        a.addAll(alphabetTemp);
                        tablatransicionesTemp[cont][a.indexOf(s)].addAll(clau2);
                    }
                }
            }
        }

        TreeSet<Integer> f = cerrarVacias(etatinitial);
        boolean cq0F = false;

        for (Integer i : etatfinal) {
            if (f.contains(i)) {
                cq0F = true;
            }
        }

        if (cq0F) {
            etatfinal.add(etatinitial);
        }
        alphabet = alphabetTemp;
        tabtrans = tablatransicionesTemp;

        System.out.println();
    }

    private TreeSet<Integer> cerrarVacias(int q) {
        TreeSet<Integer> cierre = new TreeSet<Integer>();
        TreeSet<Integer> ts = new TreeSet<Integer>();
        Stack<TreeSet<Integer>> pila = new Stack<TreeSet<Integer>>();
        pila.push(obtenirTransition(q, "E"));
        cierre.add(q);

        while (!pila.isEmpty()) {
            ts = pila.pop();

            for (Integer i : ts) {
                if (!cierre.contains(i.intValue())) {
                    pila.push(obtenirTransition(i.intValue(), "E"));
                }
            }
            cierre.addAll(ts);
        }
        return cierre;
    }

    private TreeSet<Integer> obtenirTransition(int q0, String e) {
        Vector<String> a = new Vector<String>();
        a.addAll(alphabet);
        return tabtrans[q0][a.indexOf(e)];
    }

    private boolean verificationMinimale() {
        boolean f = true;

        int[][] etat = new int[etatsnum][etatsnum];
        TreeSet<Integer> r;
        TreeSet<Integer> t;
        int y;
        int x;
        int taille = 0;
        for (int cont = 1; cont < etatsnum; cont++) {
            for (int cont2 = 0; cont2 < cont; cont2++) {
                if ((etatfinal.contains(cont) && !etatfinal.contains(cont2)) || (etatfinal.contains(cont2) && !etatfinal.contains(cont))) {
                    etat[cont][cont2] = 1;
                }
                taille = 0;
                for (String s : alphabet) {
                    r = obtenirTransition(cont, s);
                    t = obtenirTransition(cont2, s);
                    if (r.size() > 0 && t.size() > 0) {
                        taille++;
                        y = r.first().intValue();
                        x = t.first().intValue();

                        if (x < y) {
                            if (etat[y][x] == 1) {
                                etat[cont][cont2] = 1;
                            }
                        } else {
                            if (etat[x][y] == 1) {
                                etat[cont][cont2] = 1;
                            }
                        }
                        if (y != x) {
                            etat[cont][cont2] = 1;
                        }
                    }
                }
                if (taille != alphabet.size()) {
                    etat[cont][cont2] = 1;
                }
            }
        }

        for (int cont = 1; cont < etatsnum; cont++) {
            for (int cont2 = 0; cont2 < cont; cont2++) {
                if (etat[cont][cont2] == 0) {
                    f = false;
                }
            }
        }

        return f;

    }

    private void minimiser() {

        int[][] etat = new int[etatsnum][etatsnum];
        TreeSet<Integer> r;
        TreeSet<Integer> t;
        int x;
        int y;
        int taille = 0;
        for (int cont = 1; cont < etatsnum; cont++) {
            for (int cont2 = 0; cont2 < cont; cont2++) {
                if ((etatfinal.contains(cont) && !etatfinal.contains(cont2)) || (etatfinal.contains(cont2) && !etatfinal.contains(cont))) {
                    etat[cont][cont2] = 1;
                }
                taille = 0;
                for (String s : alphabet) {
                    r = obtenirTransition(cont, s);
                    t = obtenirTransition(cont2, s);
                    if (r.size() > 0 && t.size() > 0) {
                        taille++;
                        x = r.first().intValue();
                        y = t.first().intValue();

                        if (y < x) {
                            if (etat[x][y] == 1) {
                                etat[cont][cont2] = 1;
                            }
                        } else {
                            if (etat[y][x] == 1) {
                                etat[cont][cont2] = 1;
                            }
                        }
                        if (x != y) {
                            etat[cont][cont2] = 1;
                        }
                    }
                }
                if (taille != alphabet.size()) {
                    etat[cont][cont2] = 1;
                }
            }
        }
        Vector<TreeSet> vector = new Vector<TreeSet>();
        TreeSet<Integer> ts;
        boolean f;

        for (int cont = 1; cont < etatsnum; cont++) {
            for (int cont2 = 0; cont2 < cont; cont2++) {
                if (etat[cont][cont2] == 0) {
                    ts = new TreeSet<Integer>();
                    f = true;

                    ts.add(cont);
                    ts.add(cont2);

                    for (TreeSet<Integer> tsmod : vector) {
                        if (tsmod.contains(cont) || tsmod.contains(cont)) {
                            tsmod.addAll(ts);
                            f = false;
                        }
                    }
                    if (f) {
                        vector.add(ts);
                    }
                }
            }
        }

        f = true;

        for (int cont = 0; cont < etatsnum; cont++) {
            f = true;
            for (TreeSet<Integer> tsmod : vector) {
                if (tsmod.contains(cont)) {
                    f = false;
                }
            }
            if (f) {
                ts = new TreeSet<Integer>();
                ts.add(cont);
                vector.add(ts);
            }
        }

        TreeSet<Integer>[][] tablaTemp = new TreeSet[vector.size()][alphabet.size()];

        TreeSet<Integer> tran;
        int t0;
        TreeSet<Integer> t1;
        for (String s : alphabet) {
            for (TreeSet<Integer> tsi : vector) {
                tran = new TreeSet<Integer>();
                for (Integer i : tsi) {
                    tran.addAll(obtenirTransition(i, s));
                }

                t0 = vector.indexOf(tsi);
                t1 = new TreeSet<Integer>();
                for (TreeSet<Integer> tsi2 : vector) {
                    if (tran.size() > 0 && tsi2.containsAll(tran)) {
                        t1.add(vector.indexOf(tsi2));
                    }
                }

                Vector<String> a = new Vector<String>();
                a.addAll(alphabet);
                tablaTemp[t0][a.indexOf(s)] = t1;

            }
        }

        TreeSet<Integer> finales = new TreeSet<Integer>();
        int q00 = etatinitial;

        for (TreeSet<Integer> i : vector) {
            if (i.contains(etatinitial)) {
                q00 = vector.indexOf(i);
            }

            for (Integer ii : etatfinal) {
                if (i.contains(ii)) {
                    finales.add(vector.indexOf(i));
                }
            }
        }

        etatinitial = q00;
        etatsnum = vector.size();
        etatfinal = finales;
        tabtrans = tablaTemp;

        System.out.println();

    }
}
