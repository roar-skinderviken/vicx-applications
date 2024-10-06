import unittest

from src.kmeans_grades import map_scores_to_grades


class TestKmeansGrades(unittest.TestCase):
    def test_map_scores_to_grades_given_valid_input_expect_result(self):
        result = map_scores_to_grades(
            fail_score=30,
            scores=[20, 30, 60, 65, 90, 91, 99]
        )

        self.assertEqual(
            {
                20: "Failed",
                30: "E",
                60: "D",
                65: "C",
                90: "B",
                91: "B",
                99: "A"
            }, result)

    def test_map_scores_to_grades_given_duplicate_scores_expect_result(self):
        result = map_scores_to_grades(fail_score=30, scores=[80, 80, 80, 80, 80])
        self.assertEqual({80: "A"}, result)

    def test_map_scores_to_grades_given_failed_scores_only_expect_result(self):
        result = map_scores_to_grades(fail_score=30, scores=[20])
        self.assertEqual({20: "Failed"}, result)

    def test_map_scores_to_grades_given_no_scores_expect_empty_result(self):
        result = map_scores_to_grades(fail_score=30, scores=[])
        self.assertEqual({}, result)


if __name__ == '__main__':
    unittest.main()
