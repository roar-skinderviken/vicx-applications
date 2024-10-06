from sklearn.cluster import KMeans

GRADES = ["A", "B", "C", "D", "E"]


def map_scores_to_grades(
        fail_score: int,
        scores: list[int]
) -> dict[int, str]:
    unique_scores = list(set(scores))
    valid_scores = [[score] for score in unique_scores if score >= fail_score]
    failed_scores = {score: "Failed" for score in unique_scores if score < fail_score}

    if len(valid_scores) < 1:
        return failed_scores

    n_clusters = min(5, len(valid_scores))

    # Apply KMeans to group scores into max 5 clusters for grades A, B, C, D, E
    # noinspection PyTypeChecker
    kmeans: KMeans = KMeans(n_clusters=n_clusters, random_state=0).fit(valid_scores)

    # Sort the clusters based on their centroids
    centroids = kmeans.cluster_centers_.flatten()
    sorted_indices = sorted(range(len(centroids)), key=lambda i: centroids[i], reverse=True)

    # Map the sorted clusters to grades
    grade_mapping = {sorted_indices[i]: GRADES[i] for i in range(n_clusters)}

    # Assign grades based on clustering results
    graded_scores = {score[0]: grade_mapping[kmeans.predict([score])[0]] for score in valid_scores}

    # Merge failed and graded scores
    result = {**graded_scores, **failed_scores}

    return result
