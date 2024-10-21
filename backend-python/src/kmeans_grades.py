from sklearn.cluster import KMeans
import numpy as np

GRADES = ["A", "B", "C", "D", "E"]


def map_scores_to_grades(fail_score: float, max_score: float, scores: list[float], max_iter: int) -> dict:
    # Separate passing and failing grades
    pass_grades = [[score] for score in scores if score >= fail_score]
    fail_grades = {score: "F" for score in scores if score < fail_score}

    if len(pass_grades) < 5:
        return {"error": "Not enough passing grades to assign A, B, C, D, E."}

    # KMeans clustering on passing grades
    kmeans = KMeans(n_clusters=5, max_iter=max_iter)
    kmeans.fit(pass_grades)
    labels = kmeans.labels_

    # Assign letter grades (A, B, C, D, E)
    sorted_clusters = {idx: GRADES[i] for i, (center, idx) in
                       enumerate(sorted(zip(kmeans.cluster_centers_, range(5)), reverse=True))}

    # Combine scores with their letter grades
    graded_scores = {score[0]: sorted_clusters[label] for score, label in zip(pass_grades, labels)}

    # Merge passing and failing grades
    result = {**graded_scores, **fail_grades}

    # Sort output by score
    result_sorted = dict(sorted(result.items(), key=lambda item: item[0], reverse=True))

    return result_sorted
