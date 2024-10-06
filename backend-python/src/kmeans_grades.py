from sklearn.cluster import KMeans

GRADES = ["E", "D", "C", "B", "A"]


def map_scores_to_grades(
        fail_score: int,
        scores: list[int]
) -> dict[int, str]:
    # Keep only scores that are above or equal to the fail score
    valid_scores = [[score] for score in scores if score >= fail_score]
    failed_scores = {score: "Failed" for score in scores if score < fail_score}

    if len(valid_scores) < 1:
        return failed_scores

    n_clusters = min(5, len(valid_scores))

    # Apply KMeans to group scores into max 5 clusters for grades A, B, C, D, E
    # noinspection PyTypeChecker
    kmeans: KMeans = KMeans(n_clusters=n_clusters, random_state=0).fit(valid_scores)

    # Sort the clusters based on their centroids
    centroids = kmeans.cluster_centers_.flatten()
    sorted_indices = sorted(range(len(centroids)), key=lambda i: centroids[i])

    # Map the sorted clusters to grades
    adjusted_grades = GRADES[-len(sorted_indices):]  # Adjust the grades list to match the number of valid scores
    grade_mapping = {sorted_indices[i]: grade for i, grade in enumerate(adjusted_grades)}

    # Assign grades based on clustering results
    graded_scores = {score[0]: grade_mapping[kmeans.predict([score])[0]] for score in valid_scores}

    # Merge failed and graded scores
    result = {**failed_scores, **graded_scores}

    return result
