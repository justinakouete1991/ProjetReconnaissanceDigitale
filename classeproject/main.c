#include <stdio.h>
#include <stdlib.h>
#include <string.h>

#define MAX_EMPLOYES 100

typedef struct {
    char nom[50];
    char prenom[50];
    int matricule;
    float salaire;
} Employe;

int main() {
    Employe employes[MAX_EMPLOYES];
    int n, i;
    float somme = 0.0, moyenne;
    int count_sup_moyenne = 0;

    FILE *fichier = fopen("employes.txt", "w");
    if (fichier == NULL) {
        printf("Erreur lors de l'ouverture du fichier.\n");
        return 1;
    }

    printf("Combien d'employ�s voulez-vous enregistrer,svp ? ");
    scanf("%d", &n);

    for (i = 0; i < n; i++) {
        printf("\n--- Employ� %d ---\n", i + 1);
        printf("Nom : ");
        scanf("%s", employes[i].nom);
        printf("Pr�nom : ");
        scanf("%s", employes[i].prenom);
        printf("Matricule : ");
        scanf("%d", &employes[i].matricule);
        printf("Salaire : ");
        scanf("%f", &employes[i].salaire);

        // �criture dans le fichier
        fprintf(fichier, "%s %s %d %.2f\n", employes[i].nom, employes[i].prenom,
                employes[i].matricule, employes[i].salaire);

        somme += employes[i].salaire;
    }

    fclose(fichier);

    moyenne = somme / n;
    printf("\nSalaire moyen : %.2f\n", moyenne);

    for (i = 0; i < n; i++) {
        if (employes[i].salaire > moyenne) {
            count_sup_moyenne++;
        }
    }

    printf("Nombre d'employ�s dont le salaire est sup�rieur � la moyenne : %d\n", count_sup_moyenne);

    return 0;
}
